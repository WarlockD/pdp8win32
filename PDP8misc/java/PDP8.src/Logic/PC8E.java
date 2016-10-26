/*   1:    */ package Logic;
/*   2:    */ 
/*   3:    */ import Devices.PTape;
/*   4:    */ import Devices.PtrPtp;
/*   5:    */ import java.io.IOException;
/*   6:    */ import java.io.PrintStream;
/*   7:    */ import java.io.RandomAccessFile;
/*   8:    */ 
/*   9:    */ public class PC8E
/*  10:    */   implements Device, Constants
/*  11:    */ {
/*  12: 19 */   public static int DevId01 = 1;
/*  13: 20 */   public static int DevId02 = 2;
/*  14:    */   public PtrPtp ptrptp;
/*  15:    */   public BusRegMem data;
/*  16: 24 */   private int unit = 0;
/*  17: 25 */   private boolean rdrflag = false;
/*  18: 26 */   private boolean rdrrun = false;
/*  19: 27 */   public boolean fetching = false;
/*  20: 28 */   public boolean sevenbit = false;
/*  21: 29 */   public boolean punhispeed = true;
/*  22: 30 */   public boolean rdrhispeed = true;
/*  23: 31 */   private boolean punflag = false;
/*  24: 32 */   private boolean prpintena = true;
/*  25: 33 */   private int rdrbuf = 0;
/*  26: 34 */   public boolean[] havetape = { false, false };
/*  27: 35 */   public boolean[] sel = { false, false };
/*  28: 36 */   public RandomAccessFile[] tape = { null, null };
/*  29: 37 */   public int[] line = { 0, 0 };
/*  30:    */   private VirTimer puntim;
/*  31:    */   private VirTimer rdrtim;
/*  32:    */   private VirTimer stoptim;
/*  33:    */   
/*  34:    */   public PC8E(BusRegMem data)
/*  35:    */   {
/*  36: 46 */     this.data = data;
/*  37: 47 */     this.ptrptp = new PtrPtp(this);
/*  38: 48 */     this.ptrptp.setVisible(true);
/*  39:    */     
/*  40: 50 */     VirListener punaction = new VirListener()
/*  41:    */     {
/*  42:    */       public void actionPerformed()
/*  43:    */       {
/*  44: 52 */         PC8E.this.unit = 1;
/*  45: 53 */         PC8E.this.setPUN(true);
/*  46:    */       }
/*  47: 55 */     };
/*  48: 56 */     this.puntim = new VirTimer(data.virqueue, 200000, punaction);
/*  49: 57 */     this.puntim.setRepeats(false);
/*  50:    */     
/*  51: 59 */     VirListener rdraction = new VirListener()
/*  52:    */     {
/*  53:    */       public void actionPerformed()
/*  54:    */       {
/*  55: 61 */         PC8E.this.unit = 0;
/*  56: 62 */         PC8E.this.setRDR(true);
/*  57:    */       }
/*  58: 64 */     };
/*  59: 65 */     this.rdrtim = new VirTimer(data.virqueue, 33333, rdraction);
/*  60: 66 */     this.rdrtim.setRepeats(false);
/*  61:    */     
/*  62: 68 */     VirListener stopaction = new VirListener()
/*  63:    */     {
/*  64:    */       public void actionPerformed()
/*  65:    */       {
/*  66: 70 */         PC8E.this.unit = 0;
/*  67: 71 */         PC8E.this.setSTOP(true);
/*  68:    */       }
/*  69: 73 */     };
/*  70: 74 */     this.stoptim = new VirTimer(data.virqueue, 400000, stopaction);
/*  71: 75 */     this.stoptim.setRepeats(false);
/*  72:    */   }
/*  73:    */   
/*  74:    */   public void Decode(int devcode, int opcode)
/*  75:    */   {
/*  76: 79 */     if (devcode == 1)
/*  77:    */     {
/*  78: 80 */       this.unit = 0;
/*  79: 81 */       switch (opcode)
/*  80:    */       {
/*  81:    */       case 0: 
/*  82:    */         break;
/*  83:    */       case 1: 
/*  84:    */         break;
/*  85:    */       case 2: 
/*  86: 84 */         this.data.c0c1c2 = 2; break;
/*  87:    */       case 4: 
/*  88:    */         break;
/*  89:    */       case 6: 
/*  90: 86 */         this.data.c0c1c2 = 2;
/*  91:    */       }
/*  92:    */     }
/*  93: 89 */     if (devcode == 2)
/*  94:    */     {
/*  95: 90 */       this.unit = 1;
/*  96: 91 */       switch (opcode)
/*  97:    */       {
/*  98:    */       case 0: 
/*  99:    */         break;
/* 100:    */       case 1: 
/* 101:    */         break;
/* 102:    */       case 2: 
/* 103:    */         break;
/* 104:    */       case 4: 
/* 105:    */         break;
/* 106:    */       }
/* 107:    */     }
/* 108:    */   }
/* 109:    */   
/* 110:    */   public void Execute(int devcode, int opcode)
/* 111:    */   {
/* 112:102 */     if (devcode == 1)
/* 113:    */     {
/* 114:103 */       this.unit = 0;
/* 115:104 */       switch (opcode)
/* 116:    */       {
/* 117:    */       case 0: 
/* 118:105 */         this.prpintena = true;setIntReq(); break;
/* 119:    */       case 1: 
/* 120:106 */         this.data.skipbus = (this.rdrflag); break;
/* 121:    */       case 2: 
/* 122:107 */         this.data.data |= this.rdrbuf;setRDR(false); break;
/* 123:    */       case 4: 
/* 124:108 */         setRDR(false);getChar(); break;
/* 125:    */       case 6: 
/* 126:109 */         this.data.data |= this.rdrbuf;setRDR(false);getChar();
/* 127:    */       }
/* 128:    */     }
/* 129:112 */     if (devcode == 2)
/* 130:    */     {
/* 131:113 */       this.unit = 1;
/* 132:114 */       switch (opcode)
/* 133:    */       {
/* 134:    */       case 0: 
/* 135:115 */         this.prpintena = false;clearIntReq(); break;
/* 136:    */       case 1: 
/* 137:116 */         this.data.skipbus = (this.punflag); break;
/* 138:    */       case 2: 
/* 139:117 */         setPUN(false); break;
/* 140:    */       case 4: 
/* 141:118 */         punChar(this.data.data); break;
/* 142:    */       case 6: 
/* 143:119 */         setPUN(false);punChar(this.data.data);
/* 144:    */       }
/* 145:    */     }
/* 146:    */   }
/* 147:    */   
/* 148:    */   public void clearIntReq()
/* 149:    */   {
/* 150:125 */     if (((!this.rdrflag ? 1 : 0) & (!this.punflag ? 1 : 0)) != 0) {
/* 151:126 */       this.data.setIntReq(DevId01, false);
/* 152:    */     }
/* 153:    */   }
/* 154:    */   
/* 155:    */   public void setIntReq()
/* 156:    */   {
/* 157:131 */     if (((this.rdrflag == true ? 1 : 0) | (this.punflag == true ? 1 : 0)) != 0) {
/* 158:132 */       this.data.setIntReq(DevId01, this.prpintena);
/* 159:    */     }
/* 160:    */   }
/* 161:    */   
/* 162:    */   private void getChar()
/* 163:    */   {
/* 164:137 */     if (this.havetape[this.unit] != 0)
/* 165:    */     {
/* 166:138 */       if (!this.fetching)
/* 167:    */       {
/* 168:139 */         this.rdrtim.setInitialDelay(2000);
/* 169:140 */         this.rdrtim.start();
/* 170:    */       }
/* 171:142 */       this.rdrrun = true;
/* 172:    */     }
/* 173:    */     else
/* 174:    */     {
/* 175:144 */       this.rdrtim.stop();
/* 176:145 */       this.rdrrun = false;
/* 177:    */     }
/* 178:    */   }
/* 179:    */   
/* 180:    */   private void setRDR(boolean set)
/* 181:    */   {
/* 182:151 */     if (set)
/* 183:    */     {
/* 184:152 */       if (this.rdrrun)
/* 185:    */       {
/* 186:153 */         this.rdrrun = false;
/* 187:154 */         this.rdrbuf = getLine();
/* 188:155 */         if (this.rdrbuf >= 0)
/* 189:    */         {
/* 190:156 */           this.rdrflag = true;
/* 191:157 */           setIntReq();
/* 192:    */           int speed;
/* 193:    */           int speed;
/* 194:158 */           if (this.rdrhispeed) {
/* 195:158 */             speed = 3333;
/* 196:    */           } else {
/* 197:159 */             speed = 33333;
/* 198:    */           }
/* 199:160 */           this.rdrtim.setInitialDelay(speed);
/* 200:161 */           this.rdrtim.start();
/* 201:162 */           this.fetching = true;
/* 202:    */         }
/* 203:    */         else
/* 204:    */         {
/* 205:164 */           this.rdrbuf = 255;
/* 206:    */         }
/* 207:    */       }
/* 208:    */       else
/* 209:    */       {
/* 210:166 */         this.rdrtim.stop();
/* 211:167 */         this.stoptim.start();
/* 212:168 */         this.fetching = true;
/* 213:    */       }
/* 214:    */     }
/* 215:    */     else
/* 216:    */     {
/* 217:171 */       this.rdrflag = false;
/* 218:172 */       clearIntReq();
/* 219:    */     }
/* 220:    */   }
/* 221:    */   
/* 222:    */   private void setSTOP(boolean set)
/* 223:    */   {
/* 224:177 */     this.fetching = false;
/* 225:178 */     if (this.rdrrun)
/* 226:    */     {
/* 227:179 */       this.rdrrun = false;
/* 228:180 */       this.rdrbuf = getLine();
/* 229:181 */       if (this.rdrbuf >= 0)
/* 230:    */       {
/* 231:182 */         this.rdrflag = true;
/* 232:183 */         setIntReq();
/* 233:    */       }
/* 234:    */       else
/* 235:    */       {
/* 236:185 */         this.rdrbuf = 255;
/* 237:    */       }
/* 238:    */     }
/* 239:    */   }
/* 240:    */   
/* 241:    */   private void punChar(int data)
/* 242:    */   {
/* 243:    */     int punchbyte;
/* 244:    */     int punchbyte;
/* 245:192 */     if (this.sevenbit) {
/* 246:192 */       punchbyte = data & 0x7F;
/* 247:    */     } else {
/* 248:193 */       punchbyte = data & 0xFF;
/* 249:    */     }
/* 250:194 */     this.ptrptp.pPunch1.setHole(punchbyte);
/* 251:195 */     if (setLine(punchbyte) == 0)
/* 252:    */     {
/* 253:    */       int speed;
/* 254:    */       int speed;
/* 255:196 */       if (this.punhispeed) {
/* 256:196 */         speed = 20000;
/* 257:    */       } else {
/* 258:196 */         speed = 200000;
/* 259:    */       }
/* 260:197 */       this.puntim.setInitialDelay(speed);
/* 261:198 */       this.puntim.start();
/* 262:    */     }
/* 263:    */   }
/* 264:    */   
/* 265:    */   private void setPUN(boolean set)
/* 266:    */   {
/* 267:203 */     if (set)
/* 268:    */     {
/* 269:204 */       this.punflag = true;
/* 270:    */       
/* 271:206 */       setIntReq();
/* 272:    */     }
/* 273:    */     else
/* 274:    */     {
/* 275:208 */       this.puntim.stop();
/* 276:209 */       this.punflag = false;
/* 277:210 */       clearIntReq();
/* 278:    */     }
/* 279:    */   }
/* 280:    */   
/* 281:    */   private int getLine()
/* 282:    */   {
/* 283:215 */     int newbyte = 0;
/* 284:216 */     if (((this.havetape[this.unit] == 0 ? 1 : 0) | (this.sel[this.unit] == 0 ? 1 : 0)) != 0) {
/* 285:216 */       return -1;
/* 286:    */     }
/* 287:    */     try
/* 288:    */     {
/* 289:218 */       this.tape[this.unit].seek(this.line[this.unit]);
/* 290:219 */       newbyte = this.tape[this.unit].read();
/* 291:220 */       if (newbyte < 0) {
/* 292:220 */         this.havetape[this.unit] = false;
/* 293:    */       } else {
/* 294:221 */         this.line[this.unit] += 1;
/* 295:    */       }
/* 296:    */     }
/* 297:    */     catch (IOException e)
/* 298:    */     {
/* 299:223 */       System.out.println(e);
/* 300:    */     }
/* 301:225 */     return newbyte;
/* 302:    */   }
/* 303:    */   
/* 304:    */   private int setLine(int newbyte)
/* 305:    */   {
/* 306:230 */     if (((this.havetape[this.unit] == 0 ? 1 : 0) | (this.sel[this.unit] == 0 ? 1 : 0)) != 0) {
/* 307:230 */       return -1;
/* 308:    */     }
/* 309:    */     try
/* 310:    */     {
/* 311:232 */       this.tape[this.unit].seek(this.line[this.unit]);
/* 312:233 */       this.tape[this.unit].write(newbyte);
/* 313:234 */       this.line[this.unit] += 1;
/* 314:    */     }
/* 315:    */     catch (IOException e)
/* 316:    */     {
/* 317:236 */       System.out.println(e);
/* 318:    */     }
/* 319:238 */     return 0;
/* 320:    */   }
/* 321:    */   
/* 322:    */   public void ClearFlags(int devcode)
/* 323:    */   {
/* 324:242 */     this.rdrtim.stop();
/* 325:243 */     this.stoptim.stop();
/* 326:244 */     this.rdrflag = false;
/* 327:245 */     this.puntim.stop();
/* 328:246 */     this.punflag = false;
/* 329:247 */     this.fetching = false;
/* 330:248 */     this.rdrrun = false;
/* 331:249 */     this.data.setIntReq(DevId01, false);
/* 332:250 */     this.prpintena = true;
/* 333:    */   }
/* 334:    */   
/* 335:    */   public void Interrupt(int command) {}
/* 336:    */   
/* 337:    */   public void ClearRun(boolean run)
/* 338:    */   {
/* 339:258 */     if (!run) {}
/* 340:    */   }
/* 341:    */   
/* 342:    */   public void CloseDev(int devcode)
/* 343:    */   {
/* 344:263 */     this.ptrptp.closeTape(0);
/* 345:264 */     this.ptrptp.closeTape(1);
/* 346:    */   }
/* 347:    */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Logic.PC8E
 * JD-Core Version:    0.7.0.1
 */