/*   1:    */ package Logic;
/*   2:    */ 
/*   3:    */ import Devices.Terminal;
/*   4:    */ import java.io.InputStream;
/*   5:    */ import java.io.OutputStream;
/*   6:    */ import java.io.PrintStream;
/*   7:    */ 
/*   8:    */ public class TTY
/*   9:    */   implements Device, Constants
/*  10:    */ {
/*  11: 18 */   public static int DevId03 = 3;
/*  12: 19 */   public static int DevId04 = 4;
/*  13:    */   public BusRegMem data;
/*  14:    */   public Terminal console;
/*  15: 24 */   private int punchbyte = 0;
/*  16: 25 */   private boolean tlsflag = false;
/*  17: 26 */   private boolean punchflag = false;
/*  18: 27 */   private int readerbyte = 0;
/*  19: 28 */   private volatile boolean krbflag = false;
/*  20: 29 */   private boolean readerflag = false;
/*  21: 30 */   private boolean KCCFlag = false;
/*  22: 31 */   private boolean ttyinten = true;
/*  23: 32 */   private boolean terminal = true;
/*  24:    */   private OutputStream out;
/*  25:    */   private Thread output;
/*  26:    */   private InputStream in;
/*  27:    */   private Thread input;
/*  28: 39 */   protected Object lockTSF = new Object();
/*  29: 40 */   protected Object lockKRB = new Object();
/*  30:    */   
/*  31:    */   public TTY(BusRegMem data)
/*  32:    */   {
/*  33: 44 */     this.data = data;
/*  34: 45 */     this.console = new Terminal(this);
/*  35: 46 */     VirListener vl = new VirListener()
/*  36:    */     {
/*  37:    */       public void actionPerformed()
/*  38:    */       {
/*  39: 48 */         System.out.println("Timer");
/*  40:    */       }
/*  41:    */     };
/*  42:    */   }
/*  43:    */   
/*  44:    */   public void Decode(int devcode, int opcode)
/*  45:    */   {
/*  46: 56 */     if (devcode == 3) {
/*  47: 57 */       synchronized (this.in)
/*  48:    */       {
/*  49: 58 */         switch (opcode)
/*  50:    */         {
/*  51:    */         case 0: 
/*  52: 59 */           this.krbflag = false;clearIntReq(); break;
/*  53:    */         case 1: 
/*  54:    */           break;
/*  55:    */         case 2: 
/*  56: 61 */           this.data.c0c1c2 = 3; break;
/*  57:    */         case 4: 
/*  58: 62 */           this.data.c0c1c2 = 2; break;
/*  59:    */         case 5: 
/*  60: 63 */           this.ttyinten = ((this.data.ac & 0x1) > 0); break;
/*  61:    */         case 6: 
/*  62: 64 */           this.data.c0c1c2 = 3;
/*  63:    */         }
/*  64:    */       }
/*  65: 67 */     } else if (devcode == 4) {
/*  66: 68 */       synchronized (this.lockTSF)
/*  67:    */       {
/*  68: 69 */         switch (opcode)
/*  69:    */         {
/*  70:    */         case 0: 
/*  71: 70 */           this.tlsflag = true; break;
/*  72:    */         case 1: 
/*  73:    */           break;
/*  74:    */         case 2: 
/*  75: 72 */           this.data.c0c1c2 = 0;this.tlsflag = false;clearIntReq(); break;
/*  76:    */         case 4: 
/*  77: 73 */           this.data.c0c1c2 = 0; break;
/*  78:    */         case 5: 
/*  79:    */           break;
/*  80:    */         case 6: 
/*  81: 75 */           this.data.c0c1c2 = 0;this.tlsflag = false;clearIntReq();
/*  82:    */         }
/*  83:    */       }
/*  84:    */     }
/*  85:    */   }
/*  86:    */   
/*  87:    */   public void Execute(int devcode, int opcode)
/*  88:    */   {
/*  89: 82 */     if (devcode == 3) {
/*  90: 83 */       synchronized (this.lockKRB)
/*  91:    */       {
/*  92: 84 */         switch (opcode)
/*  93:    */         {
/*  94:    */         case 0: 
/*  95:    */           break;
/*  96:    */         case 1: 
/*  97: 86 */           this.data.skipbus = (this.krbflag); break;
/*  98:    */         case 2: 
/*  99: 87 */           this.data.data = 0;this.readerbyte = 0;this.krbflag = false;clearIntReq();this.readerflag = true;this.KCCFlag = true; break;
/* 100:    */         case 4: 
/* 101: 88 */           this.data.data |= this.readerbyte; break;
/* 102:    */         case 5: 
/* 103: 89 */           setIntReq(); break;
/* 104:    */         case 6: 
/* 105: 90 */           this.data.data = this.readerbyte;this.krbflag = false;clearIntReq();this.readerflag = true;
/* 106:    */         }
/* 107:    */       }
/* 108: 93 */     } else if (devcode == 4) {
/* 109: 94 */       synchronized (this.lockTSF)
/* 110:    */       {
/* 111: 95 */         switch (opcode)
/* 112:    */         {
/* 113:    */         case 0: 
/* 114: 96 */           this.data.setIntReq(DevId03, this.ttyinten); break;
/* 115:    */         case 1: 
/* 116: 97 */           this.data.skipbus = (this.tlsflag); break;
/* 117:    */         case 2: 
/* 118:    */           break;
/* 119:    */         case 4: 
/* 120: 99 */           this.punchbyte = this.data.data;this.tlsflag = false;clearIntReq();this.punchflag = true;this.lockTSF.notifyAll(); break;
/* 121:    */         case 5: 
/* 122:100 */           this.data.skipbus = (((this.krbflag | this.tlsflag) & this.ttyinten)); break;
/* 123:    */         case 6: 
/* 124:101 */           this.punchbyte = this.data.data;this.tlsflag = false;clearIntReq();this.punchflag = true;this.lockTSF.notifyAll();
/* 125:    */         }
/* 126:    */       }
/* 127:    */     }
/* 128:    */   }
/* 129:    */   
/* 130:    */   public void clearIntReq()
/* 131:    */   {
/* 132:108 */     if (((!this.krbflag ? 1 : 0) & (!this.tlsflag ? 1 : 0)) != 0) {
/* 133:109 */       this.data.setIntReq(DevId03, false);
/* 134:    */     }
/* 135:    */   }
/* 136:    */   
/* 137:    */   public void setIntReq()
/* 138:    */   {
/* 139:114 */     if (((this.krbflag == true ? 1 : 0) | (this.tlsflag == true ? 1 : 0)) != 0) {
/* 140:115 */       this.data.setIntReq(DevId03, this.ttyinten);
/* 141:    */     }
/* 142:    */   }
/* 143:    */   
/* 144:    */   public void ClearFlags(int devcode)
/* 145:    */   {
/* 146:120 */     this.krbflag = false;
/* 147:121 */     this.tlsflag = false;
/* 148:122 */     this.punchbyte = 0;
/* 149:123 */     this.readerbyte = 0;
/* 150:124 */     this.data.setIntReq(DevId03, false);
/* 151:125 */     this.ttyinten = true;
/* 152:    */   }
/* 153:    */   
/* 154:    */   public void setInputOutputStream(InputStream in, OutputStream out)
/* 155:    */   {
/* 156:129 */     this.in = in;
/* 157:130 */     this.out = out;
/* 158:131 */     this.input = new Thread(new ReadInput(), "TTY-Input");
/* 159:    */     
/* 160:133 */     this.input.start();
/* 161:134 */     this.output = new Thread(new WriteOutput(), "TTY-Output");
/* 162:135 */     this.output.start();
/* 163:    */   }
/* 164:    */   
/* 165:    */   public void Interrupt(int command) {}
/* 166:    */   
/* 167:    */   public void setReader(boolean state)
/* 168:    */   {
/* 169:142 */     byte[] flushy = new byte[1024];
/* 170:144 */     if (state)
/* 171:    */     {
/* 172:145 */       this.terminal = false;
/* 173:    */     }
/* 174:    */     else
/* 175:    */     {
/* 176:    */       try
/* 177:    */       {
/* 178:    */         int len;
/* 179:148 */         while (this.in.available() > 0) {
/* 180:149 */           len = this.in.read(flushy, 0, 1024);
/* 181:    */         }
/* 182:    */       }
/* 183:    */       catch (Exception e)
/* 184:    */       {
/* 185:151 */         System.out.println("setReader=>" + e);
/* 186:    */       }
/* 187:152 */       this.terminal = true;
/* 188:    */     }
/* 189:154 */     ClearFlags(DevId03);
/* 190:    */   }
/* 191:    */   
/* 192:    */   public void ClearRun(boolean run) {}
/* 193:    */   
/* 194:    */   public void CloseDev(int devcode) {}
/* 195:    */   
/* 196:    */   public class ReadInput
/* 197:    */     implements Runnable
/* 198:    */   {
/* 199:    */     public ReadInput() {}
/* 200:    */     
/* 201:    */     public void run()
/* 202:    */     {
/* 203:    */       for (;;)
/* 204:    */       {
/* 205:    */         try
/* 206:    */         {
/* 207:167 */           synchronized (TTY.this.lockKRB)
/* 208:    */           {
/* 209:169 */             if (((TTY.this.readerflag | TTY.this.terminal & !TTY.this.krbflag)) && (TTY.this.in.available() > 0))
/* 210:    */             {
/* 211:170 */               TTY.this.readerbyte = TTY.this.in.read();
/* 212:171 */               if (TTY.this.terminal) {
/* 213:171 */                 TTY.this.readerbyte = (TTY.this.readerbyte | 0x80);
/* 214:    */               }
/* 215:172 */               TTY.this.readerflag = false;
/* 216:    */               
/* 217:174 */               TTY.this.KCCFlag = false;
/* 218:175 */               TTY.this.krbflag = true;
/* 219:176 */               TTY.this.data.setIntReq(TTY.DevId03, TTY.this.ttyinten);
/* 220:    */             }
/* 221:    */           }
/* 222:179 */           Thread.sleep(1L);
/* 223:180 */           continue;System.out.println("ReadInput=>" + e);
/* 224:    */         }
/* 225:    */         catch (Exception e) {}
/* 226:    */       }
/* 227:    */     }
/* 228:    */   }
/* 229:    */   
/* 230:    */   public class WriteOutput
/* 231:    */     implements Runnable
/* 232:    */   {
/* 233:    */     public WriteOutput() {}
/* 234:    */     
/* 235:    */     public void run()
/* 236:    */     {
/* 237:    */       try
/* 238:    */       {
/* 239:    */         for (;;)
/* 240:    */         {
/* 241:189 */           synchronized (TTY.this.lockTSF)
/* 242:    */           {
/* 243:190 */             if (!TTY.this.punchflag)
/* 244:    */             {
/* 245:190 */               TTY.this.lockTSF.wait(); continue;
/* 246:    */             }
/* 247:191 */             TTY.this.out.write(TTY.this.punchbyte);
/* 248:192 */             TTY.this.out.flush();
/* 249:193 */             TTY.this.punchflag = false;
/* 250:194 */             TTY.this.lockTSF.wait(8L);
/* 251:195 */             TTY.this.tlsflag = true;
/* 252:196 */             TTY.this.data.setIntReq(TTY.DevId03, TTY.this.ttyinten);
/* 253:    */           }
/* 254:    */         }
/* 255:    */       }
/* 256:    */       catch (Exception e) {}
/* 257:    */     }
/* 258:    */   }
/* 259:    */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Logic.TTY
 * JD-Core Version:    0.7.0.1
 */