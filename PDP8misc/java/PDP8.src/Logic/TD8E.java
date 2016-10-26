/*   1:    */ package Logic;
/*   2:    */ 
/*   3:    */ import Devices.Dectape;
/*   4:    */ import java.io.IOException;
/*   5:    */ import java.io.PrintStream;
/*   6:    */ import java.io.RandomAccessFile;
/*   7:    */ 
/*   8:    */ public class TD8E
/*   9:    */   implements Device, Constants
/*  10:    */ {
/*  11: 20 */   public static int DevId77 = 63;
/*  12:    */   public Dectape dectape;
/*  13:    */   public BusRegMem data;
/*  14: 24 */   private int[] datreg = new int[4];
/*  15: 25 */   private int mtrreg = 0;
/*  16: 26 */   private boolean timerr = false;
/*  17: 27 */   private boolean wloerr = false;
/*  18: 28 */   private boolean selerr = false;
/*  19: 29 */   private int dly = 0;
/*  20: 30 */   private int unit = 0;
/*  21: 31 */   private boolean go = false;
/*  22: 32 */   private boolean write = false;
/*  23: 33 */   private boolean forward = true;
/*  24: 34 */   private boolean slfflag = false;
/*  25: 35 */   private boolean qlfflag = false;
/*  26: 36 */   private boolean utsflag = false;
/*  27: 37 */   private boolean dtp0 = false;
/*  28: 38 */   private boolean dtp1 = false;
/*  29: 39 */   private int qlfcnt = 0;
/*  30: 41 */   public boolean[] wlo = { true, true };
/*  31: 42 */   public boolean[] sel = { false, false };
/*  32: 43 */   public boolean wtm = false;
/*  33: 44 */   public RandomAccessFile[] tape = { null, null };
/*  34: 45 */   public int[] line = { 0, 0 };
/*  35:    */   private VirTimer utstim;
/*  36:    */   private VirTimer slftim;
/*  37:    */   
/*  38:    */   public TD8E(BusRegMem data)
/*  39:    */   {
/*  40: 53 */     this.data = data;
/*  41: 54 */     this.dectape = new Dectape(this);
/*  42: 55 */     this.dectape.setVisible(true);
/*  43:    */     
/*  44: 57 */     VirListener uts = new VirListener()
/*  45:    */     {
/*  46:    */       public void actionPerformed()
/*  47:    */       {
/*  48: 60 */         TD8E.this.setUTS(true);
/*  49:    */       }
/*  50: 62 */     };
/*  51: 63 */     this.utstim = new VirTimer(data.virqueue, 1400000, uts);
/*  52: 64 */     this.utstim.setRepeats(false);
/*  53:    */     
/*  54: 66 */     VirListener slf = new VirListener()
/*  55:    */     {
/*  56:    */       public void actionPerformed()
/*  57:    */       {
/*  58: 68 */         TD8E.this.setSLF(true);
/*  59:    */       }
/*  60: 70 */     };
/*  61: 71 */     this.slftim = new VirTimer(data.virqueue, 250, slf);
/*  62:    */   }
/*  63:    */   
/*  64:    */   public void Decode(int devcode, int opcode)
/*  65:    */   {
/*  66: 75 */     switch (opcode)
/*  67:    */     {
/*  68:    */     case 1: 
/*  69:    */       break;
/*  70:    */     case 2: 
/*  71:    */       break;
/*  72:    */     case 3: 
/*  73:    */       break;
/*  74:    */     case 4: 
/*  75: 79 */       this.data.c0c1c2 = 1; break;
/*  76:    */     case 5: 
/*  77: 80 */       this.data.c0c1c2 = 0; break;
/*  78:    */     case 6: 
/*  79: 81 */       this.data.c0c1c2 = 3; break;
/*  80:    */     case 7: 
/*  81: 82 */       this.data.c0c1c2 = 3;
/*  82:    */     }
/*  83:    */   }
/*  84:    */   
/*  85:    */   public void Execute(int devcode, int opcode)
/*  86:    */   {
/*  87: 87 */     switch (opcode)
/*  88:    */     {
/*  89:    */     case 1: 
/*  90: 88 */       this.data.skipbus = (this.slfflag); break;
/*  91:    */     case 2: 
/*  92: 89 */       this.data.skipbus = (this.timerr); break;
/*  93:    */     case 3: 
/*  94: 90 */       this.data.skipbus = (this.qlfflag); break;
/*  95:    */     case 4: 
/*  96: 91 */       setCommand(this.data.data); break;
/*  97:    */     case 5: 
/*  98: 92 */       checkTime();setData(this.data.data);setSLF(false); break;
/*  99:    */     case 6: 
/* 100: 93 */       checkTime();this.data.data = getCommand();setSLF(false); break;
/* 101:    */     case 7: 
/* 102: 94 */       checkTime();this.data.data = getData();setSLF(false);
/* 103:    */     }
/* 104:    */   }
/* 105:    */   
/* 106:    */   private void setCommand(int xdata)
/* 107:    */   {
/* 108:100 */     int comreg = xdata & 0xF00;
/* 109:101 */     this.dly = (getCommand() & 0xE00 ^ comreg & 0xE00);
/* 110:102 */     this.unit = ((comreg & 0x800) >> 11);
/* 111:103 */     this.forward = ((comreg & 0x400) == 0);
/* 112:104 */     this.go = ((comreg & 0x200) != 0);
/* 113:105 */     this.write = ((comreg & 0x100) != 0);
/* 114:106 */     if (this.wtm)
/* 115:    */     {
/* 116:107 */       setUTS(true);
/* 117:    */     }
/* 118:108 */     else if (this.dly > 0)
/* 119:    */     {
/* 120:109 */       setUTS(false);
/* 121:110 */       if (this.go) {
/* 122:110 */         this.utstim.restart();
/* 123:    */       }
/* 124:111 */       this.mtrreg = 0;
/* 125:    */     }
/* 126:113 */     setTimerr(false);
/* 127:114 */     this.selerr = (this.sel[this.unit] == 0);
/* 128:115 */     this.wloerr = this.wlo[this.unit];
/* 129:116 */     if ((this.selerr | this.wloerr)) {
/* 130:116 */       this.write = false;
/* 131:    */     }
/* 132:117 */     this.dectape.selLamp(this.unit);
/* 133:    */   }
/* 134:    */   
/* 135:    */   private int getCommand()
/* 136:    */   {
/* 137:122 */     int comreg = this.unit << 11;
/* 138:123 */     comreg |= (this.forward ? 0 : 1) << 10;
/* 139:124 */     comreg |= (this.go ? 1 : 0) << 9;
/* 140:125 */     comreg |= (this.write ? 1 : 0) << 8;
/* 141:126 */     comreg |= (this.wloerr ? 1 : 0) << 7;
/* 142:127 */     comreg |= ((this.selerr | this.timerr) ? 1 : 0) << 6;
/* 143:128 */     comreg |= this.mtrreg & 0x3F;
/* 144:129 */     return comreg;
/* 145:    */   }
/* 146:    */   
/* 147:    */   public void setWtm(boolean set)
/* 148:    */   {
/* 149:133 */     if (set)
/* 150:    */     {
/* 151:134 */       this.wtm = true;
/* 152:135 */       setUTS(true);
/* 153:    */     }
/* 154:    */     else
/* 155:    */     {
/* 156:137 */       this.wtm = false;
/* 157:138 */       setUTS(false);
/* 158:    */     }
/* 159:    */   }
/* 160:    */   
/* 161:    */   private void setUTS(boolean set)
/* 162:    */   {
/* 163:143 */     if (set)
/* 164:    */     {
/* 165:144 */       this.mtrreg = 0;
/* 166:145 */       this.utsflag = true;
/* 167:    */       
/* 168:147 */       this.slftim.setRepeats(true);
/* 169:148 */       this.slftim.start();
/* 170:149 */       this.dtp0 = false;
/* 171:    */     }
/* 172:    */     else
/* 173:    */     {
/* 174:151 */       this.utstim.stop();
/* 175:152 */       this.utsflag = false;
/* 176:153 */       this.mtrreg = 0;
/* 177:154 */       this.slftim.setRepeats(false);
/* 178:155 */       this.slftim.stop();
/* 179:156 */       setSLF(false);
/* 180:    */     }
/* 181:    */   }
/* 182:    */   
/* 183:    */   private void setSLF(boolean set)
/* 184:    */   {
/* 185:162 */     if (set)
/* 186:    */     {
/* 187:163 */       if (this.dtp0)
/* 188:    */       {
/* 189:164 */         this.dtp0 = false;
/* 190:165 */         this.dtp1 = true;
/* 191:    */       }
/* 192:    */       else
/* 193:    */       {
/* 194:167 */         this.dtp0 = true;
/* 195:168 */         this.dtp1 = false;
/* 196:    */       }
/* 197:170 */       if ((this.qlfflag & (this.dtp1 & !this.write | this.dtp0 & this.write))) {
/* 198:170 */         setTimerr(true);
/* 199:    */       }
/* 200:171 */       if (this.dtp1)
/* 201:    */       {
/* 202:172 */         this.slfflag = (!this.slfflag);
/* 203:173 */         this.qlfcnt += 1;
/* 204:174 */         if (this.qlfcnt == 4)
/* 205:    */         {
/* 206:175 */           this.qlfflag = true;
/* 207:176 */           this.qlfcnt = 0;
/* 208:    */         }
/* 209:178 */         if (this.wtm)
/* 210:    */         {
/* 211:179 */           if (this.write)
/* 212:    */           {
/* 213:180 */             setMark(this.datreg[0] << 1 & 0x8);
/* 214:181 */             shiftData(0);
/* 215:182 */             if (this.forward) {
/* 216:182 */               this.line[this.unit] += 1;
/* 217:    */             } else {
/* 218:182 */               this.line[this.unit] -= 1;
/* 219:    */             }
/* 220:    */           }
/* 221:    */         }
/* 222:    */         else
/* 223:    */         {
/* 224:185 */           int nextline = getLine();
/* 225:186 */           if (nextline >= 0)
/* 226:    */           {
/* 227:187 */             shiftMark(nextline);
/* 228:188 */             if (this.write)
/* 229:    */             {
/* 230:189 */               setLine(this.datreg[0] & 0x7);
/* 231:190 */               shiftData(0);
/* 232:    */             }
/* 233:    */             else
/* 234:    */             {
/* 235:192 */               shiftData(nextline);
/* 236:    */             }
/* 237:194 */             if (this.forward) {
/* 238:194 */               this.line[this.unit] += 1;
/* 239:    */             } else {
/* 240:194 */               this.line[this.unit] -= 1;
/* 241:    */             }
/* 242:    */           }
/* 243:    */           else
/* 244:    */           {
/* 245:196 */             this.mtrreg = 18;
/* 246:197 */             setUTS(false);
/* 247:198 */             this.line[this.unit] = 74000;
/* 248:    */           }
/* 249:    */         }
/* 250:    */       }
/* 251:    */     }
/* 252:    */     else
/* 253:    */     {
/* 254:205 */       this.slfflag = false;
/* 255:206 */       this.qlfflag = false;
/* 256:207 */       this.qlfcnt = 0;
/* 257:    */     }
/* 258:    */   }
/* 259:    */   
/* 260:    */   private void checkTime()
/* 261:    */   {
/* 262:212 */     if ((dT() < 0 & this.dtp0 & this.write))
/* 263:    */     {
/* 264:213 */       System.out.println("dtp0err " + dT() + String.format(": %05o", new Object[] { Integer.valueOf(this.data.pc) }));
/* 265:214 */       setTimerr(true);
/* 266:    */     }
/* 267:216 */     if ((dT() < 0 & this.dtp1 & !this.write))
/* 268:    */     {
/* 269:217 */       System.out.println("dtp1err " + dT() + String.format(": %05o", new Object[] { Integer.valueOf(this.data.pc) }));
/* 270:218 */       setTimerr(true);
/* 271:    */     }
/* 272:    */   }
/* 273:    */   
/* 274:    */   private int dT()
/* 275:    */   {
/* 276:223 */     if (this.slftim.isRunning()) {
/* 277:224 */       return (int)(this.slftim.expirationTime - this.data.pdp8time);
/* 278:    */     }
/* 279:225 */     return 0;
/* 280:    */   }
/* 281:    */   
/* 282:    */   private void setTimerr(boolean set)
/* 283:    */   {
/* 284:229 */     if (set)
/* 285:    */     {
/* 286:230 */       this.timerr = true;
/* 287:231 */       this.write = false;
/* 288:    */     }
/* 289:    */     else
/* 290:    */     {
/* 291:233 */       this.timerr = false;
/* 292:    */     }
/* 293:    */   }
/* 294:    */   
/* 295:    */   private void setData(int data)
/* 296:    */   {
/* 297:238 */     this.datreg[0] = ((data & 0xE00) >> 9);
/* 298:239 */     this.datreg[1] = ((data & 0x1C0) >> 6);
/* 299:240 */     this.datreg[2] = ((data & 0x38) >> 3);
/* 300:241 */     this.datreg[3] = (data & 0x7);
/* 301:    */   }
/* 302:    */   
/* 303:    */   private int getData()
/* 304:    */   {
/* 305:245 */     return this.datreg[0] << 9 | this.datreg[1] << 6 | this.datreg[2] << 3 | this.datreg[3];
/* 306:    */   }
/* 307:    */   
/* 308:    */   private void shiftData(int nextline)
/* 309:    */   {
/* 310:249 */     this.datreg[0] = this.datreg[1];
/* 311:250 */     this.datreg[1] = this.datreg[2];
/* 312:251 */     this.datreg[2] = this.datreg[3];
/* 313:252 */     if (!this.write) {
/* 314:252 */       this.datreg[3] = (nextline & 0x7);
/* 315:    */     } else {
/* 316:252 */       this.datreg[3] = 0;
/* 317:    */     }
/* 318:    */   }
/* 319:    */   
/* 320:    */   private void shiftMark(int nextline)
/* 321:    */   {
/* 322:256 */     if (this.utsflag) {
/* 323:257 */       this.mtrreg = (0x3F & (this.mtrreg << 1 | nextline >> 3 & 0x1));
/* 324:    */     }
/* 325:    */   }
/* 326:    */   
/* 327:    */   private int getLine()
/* 328:    */   {
/* 329:262 */     int nextline = 0;
/* 330:264 */     if (((this.tape[this.unit] == null ? 1 : 0) | (this.sel[this.unit] == 0 ? 1 : 0) | (this.line[this.unit] < 0 ? 1 : 0) | (this.line[this.unit] > 1048576 ? 1 : 0)) != 0) {
/* 331:264 */       return -1;
/* 332:    */     }
/* 333:    */     try
/* 334:    */     {
/* 335:266 */       this.tape[this.unit].seek(this.line[this.unit] / 2);
/* 336:267 */       int newbyte = this.tape[this.unit].read();
/* 337:268 */       if (newbyte < 0) {
/* 338:268 */         newbyte = 0;
/* 339:    */       }
/* 340:269 */       nextline = this.line[this.unit] % 2 == 0 ? newbyte >> 4 & 0xF : newbyte & 0xF;
/* 341:270 */       if (!this.forward) {
/* 342:270 */         nextline = (nextline ^ 0xFFFFFFFF) & 0xF;
/* 343:    */       }
/* 344:    */     }
/* 345:    */     catch (IOException e)
/* 346:    */     {
/* 347:272 */       System.out.println("getLine: " + e);
/* 348:    */     }
/* 349:274 */     return nextline;
/* 350:    */   }
/* 351:    */   
/* 352:    */   private void setMark(int nextline)
/* 353:    */   {
/* 354:280 */     if (((this.tape[this.unit] == null ? 1 : 0) | (this.sel[this.unit] == 0 ? 1 : 0)) != 0) {
/* 355:280 */       return;
/* 356:    */     }
/* 357:    */     try
/* 358:    */     {
/* 359:282 */       this.tape[this.unit].seek(this.line[this.unit] / 2);
/* 360:283 */       int oldbyte = this.tape[this.unit].read();
/* 361:284 */       if (oldbyte < 0) {
/* 362:284 */         oldbyte = 0;
/* 363:    */       }
/* 364:    */       int newbyte;
/* 365:    */       int newbyte;
/* 366:285 */       if (this.line[this.unit] % 2 == 0) {
/* 367:285 */         newbyte = oldbyte & 0x8 | nextline << 4 & 0x80;
/* 368:    */       } else {
/* 369:286 */         newbyte = oldbyte & 0x80 | nextline & 0x8;
/* 370:    */       }
/* 371:287 */       this.tape[this.unit].seek(this.line[this.unit] / 2);
/* 372:288 */       this.tape[this.unit].write(newbyte);
/* 373:    */     }
/* 374:    */     catch (IOException e)
/* 375:    */     {
/* 376:290 */       System.out.println("setMark: " + e);
/* 377:    */     }
/* 378:    */   }
/* 379:    */   
/* 380:    */   private void setLine(int nextline)
/* 381:    */   {
/* 382:297 */     if (((this.tape[this.unit] == null ? 1 : 0) | (this.sel[this.unit] == 0 ? 1 : 0)) != 0) {
/* 383:297 */       return;
/* 384:    */     }
/* 385:298 */     if (!this.forward) {
/* 386:298 */       nextline = (nextline ^ 0xFFFFFFFF) & 0x7;
/* 387:    */     }
/* 388:    */     try
/* 389:    */     {
/* 390:300 */       this.tape[this.unit].seek(this.line[this.unit] / 2);
/* 391:301 */       int oldbyte = this.tape[this.unit].read();
/* 392:302 */       if (oldbyte < 0) {
/* 393:302 */         oldbyte = 0;
/* 394:    */       }
/* 395:    */       int newbyte;
/* 396:    */       int newbyte;
/* 397:303 */       if (this.line[this.unit] % 2 == 0) {
/* 398:303 */         newbyte = oldbyte & 0x8F | nextline << 4;
/* 399:    */       } else {
/* 400:304 */         newbyte = oldbyte & 0xF8 | nextline;
/* 401:    */       }
/* 402:305 */       this.tape[this.unit].seek(this.line[this.unit] / 2);
/* 403:306 */       this.tape[this.unit].write(newbyte);
/* 404:    */     }
/* 405:    */     catch (IOException e)
/* 406:    */     {
/* 407:308 */       System.out.println("setLine: " + e);
/* 408:    */     }
/* 409:    */   }
/* 410:    */   
/* 411:    */   public void ClearFlags(int devcode)
/* 412:    */   {
/* 413:313 */     this.data.setIntReq(DevId77, false);
/* 414:314 */     if (!this.wtm) {
/* 415:314 */       setUTS(false);
/* 416:    */     }
/* 417:315 */     setTimerr(false);
/* 418:316 */     this.unit = 0;
/* 419:317 */     this.forward = true;
/* 420:318 */     this.go = false;
/* 421:319 */     this.write = false;
/* 422:320 */     this.dly = 0;
/* 423:    */   }
/* 424:    */   
/* 425:    */   public void Interrupt(int command) {}
/* 426:    */   
/* 427:    */   public void ClearRun(boolean run)
/* 428:    */   {
/* 429:328 */     if (!run)
/* 430:    */     {
/* 431:329 */       this.go = false;
/* 432:330 */       this.write = false;
/* 433:    */     }
/* 434:    */   }
/* 435:    */   
/* 436:    */   public void CloseDev(int devcode)
/* 437:    */   {
/* 438:335 */     this.dectape.closeTape(0);
/* 439:336 */     this.dectape.closeTape(1);
/* 440:    */   }
/* 441:    */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Logic.TD8E
 * JD-Core Version:    0.7.0.1
 */