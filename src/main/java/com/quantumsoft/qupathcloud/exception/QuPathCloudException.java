// Copyright (C) 2019 Google LLC
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.quantumsoft.qupathcloud.exception;

/**
 * The type QuPath cloud exception.
 */
public class QuPathCloudException extends Exception {

  /**
   * Instantiates a new QuPath cloud exception.
   *
   * @param message the message
   */
  public QuPathCloudException(String message) {
    super(message);
  }

  /**
   * Instantiates a new QuPath cloud exception.
   *
   * @param e the exception
   */
  public QuPathCloudException(Throwable e) {
    super(e);
  }
}
