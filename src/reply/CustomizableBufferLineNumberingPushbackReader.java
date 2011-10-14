/**
 * Copyright (c) Rich Hickey. All rights reserved.
 * The use and distribution terms for this software are covered by the
 * Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 * which can be found in the file epl-v10.html at the root of this distribution.
 * By using this software in any fashion, you are agreeing to be bound by
 * the terms of this license.
 * You must not remove this notice, or any other, from this software.
 */

package reply;

import java.io.PushbackReader;
import java.io.Reader;
import java.io.LineNumberReader;
import java.io.IOException;

public class CustomizableBufferLineNumberingPushbackReader extends PushbackReader {
  private static final int newline = (int) '\n';

  private boolean _atLineStart = true;
  private boolean _prev;

  public CustomizableBufferLineNumberingPushbackReader(Reader r){
    super(new LineNumberReader(r));
  }

  // this constructor is the only change from clojure.lang
  public CustomizableBufferLineNumberingPushbackReader(Reader r, int sz){
    super(new LineNumberReader(r, sz));
  }

  public int getLineNumber(){
    return ((LineNumberReader) in).getLineNumber() + 1;
  }

  public int read() throws IOException{
      int c = super.read();
      _prev = _atLineStart;
      _atLineStart = (c == newline) || (c == -1);
      return c;
  }

  public void unread(int c) throws IOException{
      super.unread(c);
      _atLineStart = _prev;
  }

  public String readLine() throws IOException{
      int c = read();
      String line;
      switch (c) {
      case -1:
          line = null;
          break;
      case newline:
          line = "";
          break;
      default:
          String first = String.valueOf((char) c);
          String rest = ((LineNumberReader)in).readLine();
          line = (rest == null) ? first : first + rest;
          _prev = false;
          _atLineStart = true;
          break;
      }
      return line;
  }

  public boolean atLineStart(){
      return _atLineStart;
  }
}
