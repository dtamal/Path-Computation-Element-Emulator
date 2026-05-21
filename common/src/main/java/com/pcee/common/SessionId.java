/**
 * This file is part of Path Computation Element Emulator (PCEE).
 *
 * <p>PCEE is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * <p>PCEE is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * <p>You should have received a copy of the GNU General Public License along with PCEE. If not, see
 * http://www.gnu.org/licenses/.
 */
package com.pcee.common;

/**
 * Implemented as Singleton. Range: 1-255 (8 Bit)
 *
 * @author mdr
 */
public enum SessionId {
  INSTANCE; // The single instance of the enum

  private int idCounter = 0;

  // The getID method still needs to be synchronized because idCounter is mutable state
  // shared by all threads accessing the single INSTANCE.
  public synchronized int getId() {
    idCounter = (idCounter + 1) % 256;
    if (idCounter == 0) {
      idCounter++;
    }
    return idCounter;
  }
}
