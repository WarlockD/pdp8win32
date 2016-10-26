/*  1:   */ package Devices;
/*  2:   */ 
/*  3:   */ import java.io.IOException;
/*  4:   */ import java.io.InputStream;
/*  5:   */ 
/*  6:   */ public abstract class Emulator
/*  7:   */ {
/*  8:18 */   Term term = null;
/*  9:19 */   InputStream in = null;
/* 10:   */   
/* 11:   */   public Emulator(Term term, InputStream in)
/* 12:   */   {
/* 13:22 */     this.term = term;
/* 14:23 */     this.in = in;
/* 15:   */   }
/* 16:   */   
/* 17:51 */   byte[] buf = new byte[1024];
/* 18:52 */   int bufs = 0;
/* 19:53 */   int buflen = 0;
/* 20:   */   
/* 21:   */   public abstract void setPunch();
/* 22:   */   
/* 23:   */   public abstract void setLoopback(boolean paramBoolean);
/* 24:   */   
/* 25:   */   public abstract void Start();
/* 26:   */   
/* 27:   */   public abstract int[] getCodeENTER();
/* 28:   */   
/* 29:   */   public abstract int[] getCodeBS();
/* 30:   */   
/* 31:   */   public abstract int[] getCodeDEL();
/* 32:   */   
/* 33:   */   public abstract int[] getCodeESC();
/* 34:   */   
/* 35:   */   public abstract int[] getCodeUP();
/* 36:   */   
/* 37:   */   public abstract int[] getCodeDOWN();
/* 38:   */   
/* 39:   */   public abstract int[] getCodeRIGHT();
/* 40:   */   
/* 41:   */   public abstract int[] getCodeLEFT();
/* 42:   */   
/* 43:   */   public abstract int[] getCodeF1();
/* 44:   */   
/* 45:   */   public abstract int[] getCodeF2();
/* 46:   */   
/* 47:   */   public abstract int[] getCodeF3();
/* 48:   */   
/* 49:   */   public abstract int[] getCodeF4();
/* 50:   */   
/* 51:   */   public abstract int[] getCodeF5();
/* 52:   */   
/* 53:   */   public abstract int[] getCodeF6();
/* 54:   */   
/* 55:   */   public abstract int[] getCodeF7();
/* 56:   */   
/* 57:   */   public abstract int[] getCodeF8();
/* 58:   */   
/* 59:   */   public abstract int[] getCodeF9();
/* 60:   */   
/* 61:   */   public abstract int[] getCodeF10();
/* 62:   */   
/* 63:   */   public abstract void reset();
/* 64:   */   
/* 65:   */   byte getChar()
/* 66:   */     throws IOException
/* 67:   */   {
/* 68:56 */     if (this.buflen == 0) {
/* 69:57 */       fillBuf();
/* 70:   */     }
/* 71:59 */     this.buflen -= 1;
/* 72:60 */     return (byte)(this.buf[(this.bufs++)] & 0xFF);
/* 73:   */   }
/* 74:   */   
/* 75:   */   void fillBuf()
/* 76:   */     throws IOException
/* 77:   */   {
/* 78:64 */     this.buflen = (this.bufs = 0);
/* 79:65 */     this.buflen = this.in.read(this.buf, this.bufs, this.buf.length - this.bufs);
/* 80:66 */     if (this.buflen <= 0)
/* 81:   */     {
/* 82:67 */       this.buflen = 0;
/* 83:68 */       throw new IOException("fillBuf");
/* 84:   */     }
/* 85:   */   }
/* 86:   */   
/* 87:   */   void pushChar(byte foo)
/* 88:   */     throws IOException
/* 89:   */   {
/* 90:72 */     this.buflen += 1;
/* 91:73 */     this.buf[(--this.bufs)] = foo;
/* 92:   */   }
/* 93:   */   
/* 94:   */   int getASCII(int len)
/* 95:   */     throws IOException
/* 96:   */   {
/* 97:76 */     if (this.buflen == 0) {
/* 98:77 */       fillBuf();
/* 99:   */     }
/* :0:79 */     if (len > this.buflen) {
/* :1:79 */       len = this.buflen;
/* :2:   */     }
/* :3:80 */     int foo = len;
/* :4:82 */     while (len > 0)
/* :5:   */     {
/* :6:83 */       byte tmp = this.buf[(this.bufs++)];
/* :7:84 */       if ((32 <= tmp) && (tmp <= 127))
/* :8:   */       {
/* :9:85 */         this.buflen -= 1;
/* ;0:86 */         len--;
/* ;1:   */       }
/* ;2:   */       else
/* ;3:   */       {
/* ;4:89 */         this.bufs -= 1;
/* ;5:   */       }
/* ;6:   */     }
/* ;7:92 */     return foo - len;
/* ;8:   */   }
/* ;9:   */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Devices.Emulator
 * JD-Core Version:    0.7.0.1
 */