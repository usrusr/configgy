/*
 * Copyright 2009 Robey Pointer <robeypointer@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.lag.logging

import _root_.java.io.IOException
import _root_.java.net._
import _root_.java.nio.{ByteBuffer, ByteOrder}
import _root_.java.util.{Arrays, logging => javalog}
import _root_.scala.collection.mutable


object ScribeHandler {
  val OK = 0
  val TRY_LATER = 1
}

class ScribeHandler(formatter: Formatter) extends Handler(formatter) {
  // it may be necessary to log errors here if scribe is down:
  val log = Logger.get("scribe")

  // send a scribe message no more frequently than this:
  var bufferTimeMilliseconds = 100
  var lastTransmission: Long = 0

  // don't connect more frequently than this (when the scribe server is down):
  val connectBackoffMilliseconds = 15000
  var lastConnectAttempt: Long = 0

  var maxMessagesPerTransaction = 1000

  var hostname = "localhost"
  var port = 1463
  var category = "scala"

  var socket: Option[Socket] = None
  val queue = new mutable.ArrayBuffer[String]

  def server_=(server: String) {
    val parts = server.split(":", 2)
    if (parts.length == 2) {
      hostname = parts(0)
      port = parts(1).toInt
    } else {
      hostname = parts(0)
    }
  }

  def server = "%s:%d".format(hostname, port)

  private def connect() {
    if (!socket.isDefined && (System.currentTimeMillis - lastConnectAttempt > connectBackoffMilliseconds)) {
      lastConnectAttempt = System.currentTimeMillis
      try {
        socket = Some(new Socket(hostname, port))
      } catch {
        case e: Exception =>
          log.error("Unable to open socket to scribe server at %s: %s", server, e)
      }
    }
  }

  def flush() = synchronized {
    connect()
    for (s <- socket) {
      val outStream = s.getOutputStream()
      val inStream = s.getInputStream()
      val count = maxMessagesPerTransaction min queue.size
      val texts = for (i <- 0 until count) yield queue(i).getBytes("UTF-8")

      val recordHeader = ByteBuffer.wrap(new Array[Byte](10 + category.length))
      recordHeader.order(ByteOrder.BIG_ENDIAN)
      recordHeader.put(11: Byte)
      recordHeader.putShort(1)
      recordHeader.putInt(category.length)
      recordHeader.put(category.getBytes("ISO-8859-1"))
      recordHeader.put(11: Byte)
      recordHeader.putShort(2)

      val messageSize = (count * (recordHeader.capacity + 5)) + texts.foldLeft(0) { _ + _.length } + SCRIBE_PREFIX.length + 5
      val buffer = ByteBuffer.wrap(new Array[Byte](messageSize + 4))
      buffer.order(ByteOrder.BIG_ENDIAN)
      // "framing":
      buffer.putInt(messageSize)
      buffer.put(SCRIBE_PREFIX)
      buffer.putInt(count)
      for (text <- texts) {
        buffer.put(recordHeader.array)
        buffer.putInt(text.length)
        buffer.put(text)
        buffer.put(0: Byte)
      }
      buffer.put(0: Byte)

      try {
        outStream.write(buffer.array)

        // read response:
        val response = new Array[Byte](SCRIBE_REPLY.length)
        var offset = 0
        while (offset < response.length) {
          val n = inStream.read(response, offset, response.length - offset)
          if (n < 0) {
            throw new IOException("End of stream")
          }
          offset += n
        }
        if (!Arrays.equals(response, SCRIBE_REPLY)) {
          throw new IOException("Error response from scribe server: " + response.toList.toString)
        }
        queue.trimStart(count)
        if (queue.isEmpty) {
          lastTransmission = System.currentTimeMillis
        }
      } catch {
        case e: Exception =>
          log.error(e, "Failed to send %d log entries to scribe server at %s", count, server)
      }
    }
  }

  def close(): Unit = synchronized {
    for (s <- socket) {
      try {
        s.close()
      } catch {
        case _ =>
      }
    }
  }

  def publish(record: javalog.LogRecord): Unit = synchronized {
    if (record.getLoggerName == "scribe") return
    queue += getFormatter.format(record)
    if (System.currentTimeMillis - lastTransmission >= bufferTimeMilliseconds) {
      flush()
    }
  }

  val SCRIBE_PREFIX: Array[Byte] = Array(
    // version 1, call, "Log", reqid=0
    0x80.toByte, 1, 0, 1, 0, 0, 0, 3, 'L'.toByte, 'o'.toByte, 'g'.toByte, 0, 0, 0, 0,
    // list of structs
    15, 0, 1, 12
  )

  val SCRIBE_REPLY: Array[Byte] = Array(
    // version 1, reply, "Log", reqid=0
    0x80.toByte, 1, 0, 2, 0, 0, 0, 3, 'L'.toByte, 'o'.toByte, 'g'.toByte, 0, 0, 0, 0,
    // int, fid 0, 0=ok
    8, 0, 0, 0, 0, 0, 0, 0
  )
}
