/* Generated By:JJTree&JavaCC: Do not edit this line. QueryParserTokenManager.java */
package mvm.rya.indexing.accumulo.freetext.query;

/*
 * #%L
 * mvm.rya.indexing.accumulo
 * %%
 * Copyright (C) 2014 Rya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
import java.io.StringReader;

/** Token Manager. */
public class QueryParserTokenManager implements QueryParserConstants
{

  /** Debug output. */
  public  java.io.PrintStream debugStream = System.out;
  /** Set debug output. */
  public  void setDebugStream(java.io.PrintStream ds) { debugStream = ds; }
private final int jjStopStringLiteralDfa_0(int pos, long active0)
{
   switch (pos)
   {
      default :
         return -1;
   }
}
private final int jjStartNfa_0(int pos, long active0)
{
   return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0), pos + 1);
}
private int jjStopAtPos(int pos, int kind)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   return pos + 1;
}
private int jjMoveStringLiteralDfa0_0()
{
   switch(curChar)
   {
      case 40:
         return jjStopAtPos(0, 8);
      case 41:
         return jjStopAtPos(0, 9);
      default :
         return jjMoveNfa_0(2, 0);
   }
}
static final long[] jjbitVec0 = {
   0x0L, 0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL
};
private int jjMoveNfa_0(int startState, int curPos)
{
   int startsAt = 0;
   jjnewStateCnt = 24;
   int i = 1;
   jjstateSet[0] = startState;
   int kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         do
         {
            switch(jjstateSet[--i])
            {
               case 2:
                  if ((0xfffff8fcffffd9ffL & l) != 0L)
                  {
                     if (kind > 12)
                        kind = 12;
                     jjCheckNAddStates(0, 2);
                  }
                  else if (curChar == 42)
                     jjCheckNAdd(19);
                  else if (curChar == 33)
                  {
                     if (kind > 7)
                        kind = 7;
                  }
                  if (curChar == 34)
                     jjCheckNAddTwoStates(16, 17);
                  else if (curChar == 38)
                  {
                     if (kind > 5)
                        kind = 5;
                  }
                  if (curChar == 38)
                     jjstateSet[jjnewStateCnt++] = 3;
                  break;
               case 3:
                  if (curChar == 38 && kind > 5)
                     kind = 5;
                  break;
               case 4:
                  if (curChar == 38)
                     jjstateSet[jjnewStateCnt++] = 3;
                  break;
               case 5:
                  if (curChar == 38 && kind > 5)
                     kind = 5;
                  break;
               case 14:
                  if (curChar == 33 && kind > 7)
                     kind = 7;
                  break;
               case 15:
                  if (curChar == 34)
                     jjCheckNAddTwoStates(16, 17);
                  break;
               case 16:
                  if ((0xfffffffbffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(16, 17);
                  break;
               case 17:
                  if (curChar == 34 && kind > 10)
                     kind = 10;
                  break;
               case 18:
                  if (curChar == 42)
                     jjCheckNAdd(19);
                  break;
               case 19:
                  if ((0xfffff8fcffffd9ffL & l) == 0L)
                     break;
                  if (kind > 13)
                     kind = 13;
                  jjCheckNAdd(19);
                  break;
               case 20:
                  if ((0xfffff8fcffffd9ffL & l) == 0L)
                     break;
                  if (kind > 12)
                     kind = 12;
                  jjCheckNAddStates(0, 2);
                  break;
               case 21:
                  if ((0xfffff8fcffffd9ffL & l) == 0L)
                     break;
                  if (kind > 12)
                     kind = 12;
                  jjCheckNAdd(21);
                  break;
               case 22:
                  if ((0xfffff8fcffffd9ffL & l) != 0L)
                     jjCheckNAddTwoStates(22, 23);
                  break;
               case 23:
                  if (curChar == 42 && kind > 14)
                     kind = 14;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               case 2:
                  if (kind > 12)
                     kind = 12;
                  jjCheckNAddStates(0, 2);
                  if ((0x400000004000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 12;
                  else if ((0x800000008000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 6;
                  else if ((0x200000002L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 1;
                  else if (curChar == 124)
                  {
                     if (kind > 6)
                        kind = 6;
                  }
                  if (curChar == 124)
                     jjstateSet[jjnewStateCnt++] = 8;
                  break;
               case 0:
                  if ((0x1000000010L & l) != 0L && kind > 5)
                     kind = 5;
                  break;
               case 1:
                  if ((0x400000004000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 0;
                  break;
               case 6:
                  if ((0x4000000040000L & l) != 0L && kind > 6)
                     kind = 6;
                  break;
               case 7:
                  if ((0x800000008000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 6;
                  break;
               case 8:
                  if (curChar == 124 && kind > 6)
                     kind = 6;
                  break;
               case 9:
                  if (curChar == 124)
                     jjstateSet[jjnewStateCnt++] = 8;
                  break;
               case 10:
                  if (curChar == 124 && kind > 6)
                     kind = 6;
                  break;
               case 11:
                  if ((0x10000000100000L & l) != 0L && kind > 7)
                     kind = 7;
                  break;
               case 12:
                  if ((0x800000008000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 11;
                  break;
               case 13:
                  if ((0x400000004000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 12;
                  break;
               case 16:
                  jjAddStates(3, 4);
                  break;
               case 19:
                  if (kind > 13)
                     kind = 13;
                  jjstateSet[jjnewStateCnt++] = 19;
                  break;
               case 20:
                  if (kind > 12)
                     kind = 12;
                  jjCheckNAddStates(0, 2);
                  break;
               case 21:
                  if (kind > 12)
                     kind = 12;
                  jjCheckNAdd(21);
                  break;
               case 22:
                  jjCheckNAddTwoStates(22, 23);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               case 2:
                  if ((jjbitVec0[i2] & l2) == 0L)
                     break;
                  if (kind > 12)
                     kind = 12;
                  jjCheckNAddStates(0, 2);
                  break;
               case 16:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjAddStates(3, 4);
                  break;
               case 19:
                  if ((jjbitVec0[i2] & l2) == 0L)
                     break;
                  if (kind > 13)
                     kind = 13;
                  jjstateSet[jjnewStateCnt++] = 19;
                  break;
               case 21:
                  if ((jjbitVec0[i2] & l2) == 0L)
                     break;
                  if (kind > 12)
                     kind = 12;
                  jjCheckNAdd(21);
                  break;
               case 22:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjCheckNAddTwoStates(22, 23);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 24 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
static final int[] jjnextStates = {
   21, 22, 23, 16, 17, 
};

/** Token literal values. */
public static final String[] jjstrLiteralImages = {
"", null, null, null, null, null, null, null, "\50", "\51", null, null, null, 
null, null, null, };

/** Lexer state names. */
public static final String[] lexStateNames = {
   "DEFAULT",
};
static final long[] jjtoToken = {
   0x77e1L, 
};
static final long[] jjtoSkip = {
   0x1eL, 
};
protected SimpleCharStream input_stream;
private final int[] jjrounds = new int[24];
private final int[] jjstateSet = new int[48];
protected char curChar;
/** Constructor. */
public QueryParserTokenManager(SimpleCharStream stream){
   if (SimpleCharStream.staticFlag)
      throw new Error("ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");
   input_stream = stream;
}

/** Constructor. */
public QueryParserTokenManager(SimpleCharStream stream, int lexState){
   this(stream);
   SwitchTo(lexState);
}

/** Reinitialise parser. */
public void ReInit(SimpleCharStream stream)
{
   jjmatchedPos = jjnewStateCnt = 0;
   curLexState = defaultLexState;
   input_stream = stream;
   ReInitRounds();
}
private void ReInitRounds()
{
   int i;
   jjround = 0x80000001;
   for (i = 24; i-- > 0;)
      jjrounds[i] = 0x80000000;
}

/** Reinitialise parser. */
public void ReInit(SimpleCharStream stream, int lexState)
{
   ReInit(stream);
   SwitchTo(lexState);
}

/** Switch to specified lex state. */
public void SwitchTo(int lexState)
{
   if (lexState >= 1 || lexState < 0)
      throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", TokenMgrError.INVALID_LEXICAL_STATE);
   else
      curLexState = lexState;
}

protected Token jjFillToken()
{
   final Token t;
   final String curTokenImage;
   final int beginLine;
   final int endLine;
   final int beginColumn;
   final int endColumn;
   String im = jjstrLiteralImages[jjmatchedKind];
   curTokenImage = (im == null) ? input_stream.GetImage() : im;
   beginLine = input_stream.getBeginLine();
   beginColumn = input_stream.getBeginColumn();
   endLine = input_stream.getEndLine();
   endColumn = input_stream.getEndColumn();
   t = Token.newToken(jjmatchedKind, curTokenImage);

   t.beginLine = beginLine;
   t.endLine = endLine;
   t.beginColumn = beginColumn;
   t.endColumn = endColumn;

   return t;
}

int curLexState = 0;
int defaultLexState = 0;
int jjnewStateCnt;
int jjround;
int jjmatchedPos;
int jjmatchedKind;

/** Get the next Token. */
public Token getNextToken() 
{
  Token matchedToken;
  int curPos = 0;

  EOFLoop :
  for (;;)
  {
   try
   {
      curChar = input_stream.BeginToken();
   }
   catch(java.io.IOException e)
   {
      jjmatchedKind = 0;
      matchedToken = jjFillToken();
      return matchedToken;
   }

   try { input_stream.backup(0);
      while (curChar <= 32 && (0x100002600L & (1L << curChar)) != 0L)
         curChar = input_stream.BeginToken();
   }
   catch (java.io.IOException e1) { continue EOFLoop; }
   jjmatchedKind = 0x7fffffff;
   jjmatchedPos = 0;
   curPos = jjMoveStringLiteralDfa0_0();
   if (jjmatchedKind != 0x7fffffff)
   {
      if (jjmatchedPos + 1 < curPos)
         input_stream.backup(curPos - jjmatchedPos - 1);
      if ((jjtoToken[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
      {
         matchedToken = jjFillToken();
         return matchedToken;
      }
      else
      {
         continue EOFLoop;
      }
   }
   int error_line = input_stream.getEndLine();
   int error_column = input_stream.getEndColumn();
   String error_after = null;
   boolean EOFSeen = false;
   try { input_stream.readChar(); input_stream.backup(1); }
   catch (java.io.IOException e1) {
      EOFSeen = true;
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
      if (curChar == '\n' || curChar == '\r') {
         error_line++;
         error_column = 0;
      }
      else
         error_column++;
   }
   if (!EOFSeen) {
      input_stream.backup(1);
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
   }
   throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar, TokenMgrError.LEXICAL_ERROR);
  }
}

private void jjCheckNAdd(int state)
{
   if (jjrounds[state] != jjround)
   {
      jjstateSet[jjnewStateCnt++] = state;
      jjrounds[state] = jjround;
   }
}
private void jjAddStates(int start, int end)
{
   do {
      jjstateSet[jjnewStateCnt++] = jjnextStates[start];
   } while (start++ != end);
}
private void jjCheckNAddTwoStates(int state1, int state2)
{
   jjCheckNAdd(state1);
   jjCheckNAdd(state2);
}

private void jjCheckNAddStates(int start, int end)
{
   do {
      jjCheckNAdd(jjnextStates[start]);
   } while (start++ != end);
}

}
