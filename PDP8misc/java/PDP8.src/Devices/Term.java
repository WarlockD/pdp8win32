package Devices;

import java.awt.Color;

public abstract interface Term
{
  public static final int NORMAL = 0;
  public static final int BOLD = 1;
  public static final int UNDER = 2;
  public static final int BLINK = 4;
  public static final int REVERSE = 8;
  public static final int SINGLE_W_H = 16;
  public static final int DOUBLE_W = 32;
  public static final int DOUBLE_T = 64;
  public static final int DOUBLE_B = 128;
  
  public abstract int getRowCount();
  
  public abstract int getColumnCount();
  
  public abstract int getCharWidth();
  
  public abstract int getCharHeight();
  
  public abstract void setFont(int paramInt);
  
  public abstract void setCursor(int paramInt1, int paramInt2);
  
  public abstract void setLEDs(int paramInt);
  
  public abstract void clear();
  
  public abstract void draw_cursor();
  
  public abstract void redraw(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public abstract void clear_area(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public abstract void scroll_window(int paramInt1, int paramInt2, int paramInt3, Color paramColor, boolean paramBoolean);
  
  public abstract void drawBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public abstract void drawChars(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public abstract void drawString(String paramString, int paramInt1, int paramInt2);
  
  public abstract void beep();
  
  public abstract void sendKeySeq(int[] paramArrayOfInt);
  
  public abstract boolean sendOutByte(int paramInt);
  
  public abstract void setFGround(Color paramColor);
  
  public abstract void setBGround(Color paramColor);
  
  public abstract Color getFGround();
  
  public abstract Color getBGround();
}


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Devices.Term
 * JD-Core Version:    0.7.0.1
 */