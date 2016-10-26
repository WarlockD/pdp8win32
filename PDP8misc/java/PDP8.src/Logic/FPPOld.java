/*   1:    */ package Logic;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ 
/*   5:    */ public class FPPOld
/*   6:    */   implements Device, Constants
/*   7:    */ {
/*   8: 18 */   public static int DevId55 = 45;
/*   9: 19 */   public static int DevId56 = 46;
/*  10:    */   public BusRegMem data;
/*  11: 23 */   private boolean FPFlag = false;
/*  12: 24 */   private boolean FPRun = false;
/*  13: 25 */   private boolean FPPause = false;
/*  14: 26 */   private boolean FPsstep = false;
/*  15: 27 */   private boolean intena = false;
/*  16: 28 */   private static long hibit = -9223372036854775808L;
/*  17:    */   private Mode mode;
/*  18:    */   
/*  19:    */   private static enum Mode
/*  20:    */   {
/*  21: 30 */     DP(2, 1099511627776L, -1099511627776L),  FP(3, 1099511627776L, -1099511627776L),  EP(6, 16L, -16L);
/*  22:    */     
/*  23:    */     private int val;
/*  24:    */     private long lobit;
/*  25:    */     private long mask;
/*  26:    */     
/*  27:    */     private Mode(int val, long lobit, long mask)
/*  28:    */     {
/*  29: 37 */       this.val = val;
/*  30: 38 */       this.lobit = lobit;
/*  31: 39 */       this.mask = mask;
/*  32:    */     }
/*  33:    */     
/*  34:    */     public int val()
/*  35:    */     {
/*  36: 42 */       return this.val;
/*  37:    */     }
/*  38:    */     
/*  39:    */     public long lobit()
/*  40:    */     {
/*  41: 45 */       return this.lobit;
/*  42:    */     }
/*  43:    */     
/*  44:    */     public long mask()
/*  45:    */     {
/*  46: 48 */       return this.mask;
/*  47:    */     }
/*  48:    */   }
/*  49:    */   
/*  50: 52 */   private boolean underExit = false;
/*  51: 53 */   private boolean APT4K = false;
/*  52: 54 */   private boolean APTFast = false;
/*  53: 55 */   private boolean lockout = false;
/*  54: 56 */   private boolean trapExit = false;
/*  55: 57 */   private boolean hltExit = false;
/*  56: 58 */   private boolean divZero = false;
/*  57: 59 */   private boolean DPOverflow = false;
/*  58: 60 */   private boolean expOverflow = false;
/*  59: 61 */   private boolean expUnderflow = false;
/*  60: 62 */   private boolean FMInstr = false;
/*  61:    */   private Thread FPThread;
/*  62:    */   private int APTP;
/*  63:    */   private APT a;
/*  64:    */   private FAC f;
/*  65:    */   private FAC o;
/*  66:    */   
/*  67:    */   public FPPOld(BusRegMem data)
/*  68:    */   {
/*  69:100 */     this.data = data;
/*  70:101 */     this.FPThread = new Thread(new FPProc(), "FPP Thread");
/*  71:102 */     this.FPThread.start();
/*  72:103 */     this.a = new APT();
/*  73:104 */     this.f = new FAC();
/*  74:105 */     this.o = new FAC();
/*  75:    */   }
/*  76:    */   
/*  77:    */   public void Decode(int devcode, int opcode)
/*  78:    */   {
/*  79:110 */     if (devcode == DevId55) {
/*  80:111 */       switch (opcode)
/*  81:    */       {
/*  82:    */       case 1: 
/*  83:    */         break;
/*  84:    */       case 2: 
/*  85:    */         break;
/*  86:    */       case 3: 
/*  87:116 */         this.data.c0c1c2 = 0;
/*  88:117 */         break;
/*  89:    */       case 4: 
/*  90:    */         break;
/*  91:    */       case 5: 
/*  92:120 */         this.data.c0c1c2 = 0;
/*  93:121 */         break;
/*  94:    */       case 6: 
/*  95:122 */         this.data.c0c1c2 = 3;
/*  96:123 */         break;
/*  97:    */       }
/*  98:    */     }
/*  99:128 */     if (devcode == DevId56) {
/* 100:129 */       switch (opcode)
/* 101:    */       {
/* 102:    */       case 7: 
/* 103:130 */         this.data.c0c1c2 = 1;
/* 104:    */       }
/* 105:    */     }
/* 106:    */   }
/* 107:    */   
/* 108:    */   public void Execute(int devcode, int opcode)
/* 109:    */   {
/* 110:137 */     if (devcode == DevId55) {
/* 111:138 */       switch (opcode)
/* 112:    */       {
/* 113:    */       case 1: 
/* 114:139 */         this.data.skipbus = this.FPFlag;
/* 115:140 */         break;
/* 116:    */       case 2: 
/* 117:141 */         ClearFlags(DevId55);
/* 118:142 */         break;
/* 119:    */       case 3: 
/* 120:143 */         if (((!this.FPRun ? 1 : 0) & (!this.FPFlag ? 1 : 0)) != 0) {
/* 121:143 */           setCommand(this.data.data);
/* 122:    */         }
/* 123:    */         break;
/* 124:    */       case 4: 
/* 125:145 */         setHalt();
/* 126:146 */         break;
/* 127:    */       case 5: 
/* 128:147 */         if (((!this.FPRun ? 1 : 0) & (!this.FPFlag ? 1 : 0)) != 0) {
/* 129:147 */           this.data.skipbus = setStart(this.data.data);
/* 130:    */         }
/* 131:    */         break;
/* 132:    */       case 6: 
/* 133:149 */         this.data.data = getStatus();
/* 134:150 */         break;
/* 135:    */       }
/* 136:    */     }
/* 137:155 */     if (devcode == DevId56) {
/* 138:156 */       switch (opcode)
/* 139:    */       {
/* 140:    */       case 7: 
/* 141:158 */         this.mode = (((!this.FPRun ? 1 : 0) & ((this.data.data & 0x800) > 0 ? 1 : 0)) != 0 ? Mode.EP : this.mode);
/* 142:    */       }
/* 143:    */     }
/* 144:    */   }
/* 145:    */   
/* 146:    */   private void setCommand(int command)
/* 147:    */   {
/* 148:165 */     this.mode = ((command & 0x800) > 0 ? Mode.DP : Mode.FP);
/* 149:166 */     this.underExit = ((command & 0x400) > 0);
/* 150:167 */     this.APT4K = ((command & 0x200) > 0);
/* 151:168 */     this.intena = ((command & 0x100) > 0);
/* 152:169 */     this.APTFast = ((command & 0xF0) == 240);
/* 153:170 */     this.lockout = ((command & 0x8) > 0);
/* 154:171 */     this.APTP = ((command & 0x7) << 12);
/* 155:    */   }
/* 156:    */   
/* 157:    */   private void setStatus(int statreg)
/* 158:    */   {
/* 159:175 */     this.trapExit = ((statreg & 0x400) > 0);
/* 160:176 */     this.hltExit = ((statreg & 0x200) > 0);
/* 161:177 */     this.divZero = ((statreg & 0x100) > 0);
/* 162:178 */     this.DPOverflow = ((statreg & 0x80) > 0);
/* 163:179 */     this.expOverflow = ((statreg & 0x40) > 0);
/* 164:180 */     this.expUnderflow = ((statreg & 0x20) > 0);
/* 165:181 */     this.FMInstr = ((statreg & 0x10) > 0);
/* 166:    */   }
/* 167:    */   
/* 168:    */   private int getCommand()
/* 169:    */   {
/* 170:184 */     int command = 0;
/* 171:185 */     command |= (this.mode == Mode.DP ? 2048 : 0);
/* 172:186 */     command |= (this.underExit ? 1024 : 0);
/* 173:187 */     command |= (this.APT4K ? 512 : 0);
/* 174:188 */     command |= (this.intena ? 256 : 0);
/* 175:189 */     command |= (this.APTFast ? 240 : 0);
/* 176:190 */     command |= (this.lockout ? 8 : 0);
/* 177:191 */     command |= this.APTP >>> 12 & 0x7;
/* 178:192 */     return command;
/* 179:    */   }
/* 180:    */   
/* 181:    */   private int getStatus()
/* 182:    */   {
/* 183:196 */     int statreg = 0;
/* 184:197 */     statreg |= (this.mode == Mode.DP ? 2048 : 0);
/* 185:198 */     statreg |= (this.trapExit ? 1024 : 0);
/* 186:199 */     statreg |= (this.hltExit ? 512 : 0);
/* 187:200 */     statreg |= (this.divZero ? 256 : 0);
/* 188:201 */     statreg |= (this.DPOverflow ? 128 : 0);
/* 189:202 */     statreg |= (this.expOverflow ? 64 : 0);
/* 190:203 */     statreg |= (this.expUnderflow ? 32 : 0);
/* 191:204 */     statreg |= (this.FMInstr ? 16 : 0);
/* 192:205 */     statreg |= (this.lockout ? 8 : 0);
/* 193:206 */     statreg |= (this.mode == Mode.EP ? 4 : 0);
/* 194:207 */     statreg |= (this.FPPause ? 2 : 0);
/* 195:208 */     statreg |= (this.FPRun ? 1 : 0);
/* 196:209 */     return statreg;
/* 197:    */   }
/* 198:    */   
/* 199:    */   private boolean setStart(int aptp)
/* 200:    */   {
/* 201:213 */     this.APTP |= aptp & 0xFFF;
/* 202:214 */     this.a.load(this.APTP);
/* 203:215 */     this.a.setOpadd(this.a.getFPC());
/* 204:216 */     setStatus(0);
/* 205:217 */     this.FPRun = true;
/* 206:218 */     this.FPPause = false;
/* 207:219 */     return true;
/* 208:    */   }
/* 209:    */   
/* 210:    */   private void setHalt()
/* 211:    */   {
/* 212:223 */     if (this.FPRun)
/* 213:    */     {
/* 214:224 */       this.FPRun = false;
/* 215:225 */       if (this.FPPause) {
/* 216:226 */         this.a.setFPC(this.a.getFPC() - 1);
/* 217:    */       }
/* 218:    */     }
/* 219:    */     else
/* 220:    */     {
/* 221:229 */       this.FPRun = true;
/* 222:230 */       this.FPsstep = true;
/* 223:    */     }
/* 224:232 */     this.a.store(this.APTP);
/* 225:233 */     this.FPFlag = true;
/* 226:234 */     this.hltExit = true;
/* 227:    */   }
/* 228:    */   
/* 229:    */   public void clearIntReq()
/* 230:    */   {
/* 231:238 */     if (!this.FPFlag) {
/* 232:239 */       this.data.setIntReq(DevId55, false);
/* 233:    */     }
/* 234:    */   }
/* 235:    */   
/* 236:    */   public void setIntReq()
/* 237:    */   {
/* 238:244 */     if (this.FPFlag == true) {
/* 239:245 */       this.data.setIntReq(DevId55, this.intena);
/* 240:    */     }
/* 241:    */   }
/* 242:    */   
/* 243:    */   public void ClearFlags(int devcode)
/* 244:    */   {
/* 245:250 */     this.FPFlag = false;
/* 246:251 */     this.FPRun = false;
/* 247:252 */     this.FPPause = false;
/* 248:253 */     setStatus(0);
/* 249:254 */     this.data.setIntReq(DevId55, this.intena);
/* 250:    */   }
/* 251:    */   
/* 252:    */   public void Interrupt(int command) {}
/* 253:    */   
/* 254:    */   public void ClearRun(boolean run) {}
/* 255:    */   
/* 256:    */   public void CloseDev(int devcode) {}
/* 257:    */   
/* 258:    */   public class FPProc
/* 259:    */     implements Runnable
/* 260:    */   {
/* 261:    */     int addr;
/* 262:    */     
/* 263:    */     public FPProc() {}
/* 264:    */     
/* 265:    */     public void run()
/* 266:    */     {
/* 267:    */       for (;;)
/* 268:    */       {
/* 269:270 */         if ((FPPOld.this.FPRun & !FPPOld.this.FPPause))
/* 270:    */         {
/* 271:271 */           synchronized (FPPOld.this.data)
/* 272:    */           {
/* 273:272 */             int ins = FPPOld.this.a.getInstr();
/* 274:273 */             int opc = (ins & 0xE00) >> 9;
/* 275:274 */             int mod = (ins & 0x180) >> 7;
/* 276:275 */             int ext = (ins & 0x78) >> 3;
/* 277:276 */             int xr = (ins & 0x38) >> 3;
/* 278:277 */             int fxy = (ins & 0x7) >> 0;
/* 279:278 */             int bas = (ins & 0x7F) >> 0;
/* 280:279 */             int inc = (ins & 0x40) >> 6;
/* 281:280 */             System.out.printf("FPC %05o INS %04o %n", new Object[] { Integer.valueOf(FPPOld.this.a.getFPC()), Integer.valueOf(ins) });
/* 282:281 */             if (FPPOld.this.a.getFPC() == 2917) {
/* 283:282 */               this.addr = 0;
/* 284:    */             }
/* 285:284 */             switch (mod)
/* 286:    */             {
/* 287:    */             case 0: 
/* 288:286 */               switch (opc)
/* 289:    */               {
/* 290:    */               case 0: 
/* 291:288 */                 switch (ext)
/* 292:    */                 {
/* 293:    */                 case 0: 
/* 294:290 */                   FPPOld.this.fmt3(fxy); break;
/* 295:    */                 case 1: 
/* 296:    */                 case 2: 
/* 297:    */                 case 3: 
/* 298:    */                 case 4: 
/* 299:    */                 case 5: 
/* 300:293 */                   FPPOld.this.fmt3x(ext, fxy); break;
/* 301:    */                 case 8: 
/* 302:    */                 case 9: 
/* 303:295 */                   FPPOld.this.fmt2(opc, ext, fxy); break;
/* 304:    */                 case 6: 
/* 305:    */                 case 7: 
/* 306:    */                 default: 
/* 307:296 */                   System.out.println("EXT " + ext);
/* 308:    */                 }
/* 309:297 */                 break;
/* 310:    */               case 1: 
/* 311:299 */                 FPPOld.this.fmt2(opc, ext, fxy); break;
/* 312:    */               case 2: 
/* 313:301 */                 this.addr = FPPOld.this.a.getInstr();
/* 314:302 */                 FPPOld.this.a.writeIndex(xr, FPPOld.this.a.readIndex(xr) + inc);
/* 315:303 */                 if (FPPOld.this.a.readIndex(xr) > 0) {
/* 316:303 */                   FPPOld.this.a.setFPC(this.addr);
/* 317:    */                 }
/* 318:    */                 break;
/* 319:    */               case 5: 
/* 320:306 */                 FPPOld.this.f.m = (FPPOld.this.cond(ext) ? FPPOld.this.f.one : 0L);
/* 321:307 */                 break;
/* 322:    */               case 3: 
/* 323:    */               case 4: 
/* 324:    */               default: 
/* 325:308 */                 System.out.println("OPC " + opc);
/* 326:    */               }
/* 327:309 */               break;
/* 328:    */             case 1: 
/* 329:311 */               this.addr = (FPPOld.this.a.getBase() + 3 * bas);
/* 330:312 */               FPPOld.this.oper(this.addr, opc);
/* 331:313 */               break;
/* 332:    */             case 2: 
/* 333:315 */               this.addr = FPPOld.this.a.getInstr();
/* 334:316 */               FPPOld.this.a.writeIndex(xr, FPPOld.this.a.readIndex(xr) + inc);
/* 335:317 */               if (xr > 0) {
/* 336:317 */                 this.addr += FPPOld.this.mode.val() * FPPOld.this.a.readIndex(xr);
/* 337:    */               }
/* 338:318 */               FPPOld.this.oper(this.addr, opc);
/* 339:319 */               break;
/* 340:    */             case 3: 
/* 341:321 */               FPPOld.this.o.load(FPPOld.this.a.getBase() + 3 * fxy);
/* 342:322 */               FPPOld.this.a.writeIndex(xr, FPPOld.this.a.readIndex(xr) + inc);
/* 343:323 */               this.addr = ((int)(FPPOld.this.o.m >>> 40) & 0x7FFF);
/* 344:324 */               if (xr > 0) {
/* 345:324 */                 this.addr += FPPOld.this.mode.val() * FPPOld.this.a.readIndex(xr);
/* 346:    */               }
/* 347:325 */               FPPOld.this.oper(this.addr, opc);
/* 348:326 */               break;
/* 349:    */             default: 
/* 350:327 */               System.out.println("MOD " + mod);
/* 351:    */             }
/* 352:    */           }
/* 353:330 */           if (FPPOld.this.FPsstep)
/* 354:    */           {
/* 355:331 */             FPPOld.this.FPRun = false;
/* 356:332 */             FPPOld.this.FPsstep = false;
/* 357:    */           }
/* 358:    */         }
/* 359:    */         else
/* 360:    */         {
/* 361:    */           try
/* 362:    */           {
/* 363:336 */             Thread.sleep(10L);
/* 364:    */           }
/* 365:    */           catch (Exception e)
/* 366:    */           {
/* 367:338 */             System.out.println("FPProc=>" + e);
/* 368:    */           }
/* 369:    */         }
/* 370:    */       }
/* 371:    */     }
/* 372:    */   }
/* 373:    */   
/* 374:    */   private void fmt2(int opc, int ext, int fxy)
/* 375:    */   {
/* 376:345 */     int addr = this.a.getInstr() | fxy << 12;
/* 377:346 */     switch (opc)
/* 378:    */     {
/* 379:    */     case 0: 
/* 380:    */       break;
/* 381:    */     case 1: 
/* 382:349 */       switch (ext)
/* 383:    */       {
/* 384:    */       case 0: 
/* 385:    */       case 1: 
/* 386:    */       case 2: 
/* 387:    */       case 3: 
/* 388:    */       case 4: 
/* 389:    */       case 5: 
/* 390:    */       case 6: 
/* 391:    */       case 7: 
/* 392:352 */         if (cond(ext)) {
/* 393:352 */           this.a.setFPC(addr);
/* 394:    */         }
/* 395:    */         break;
/* 396:    */       case 8: 
/* 397:363 */         this.a.setIndex(addr); break;
/* 398:    */       case 9: 
/* 399:364 */         this.a.setBase(addr); break;
/* 400:    */       case 10: 
/* 401:366 */         this.a.write(addr, 536 + this.a.getFPC() >> 12);
/* 402:367 */         this.a.write(addr + 1, this.a.getFPC());
/* 403:368 */         this.a.setFPC(addr + 2);
/* 404:369 */         break;
/* 405:    */       case 11: 
/* 406:371 */         this.a.write(this.a.getBase() + 1, 536 + this.a.getFPC() >> 12);
/* 407:372 */         this.a.write(this.a.getBase() + 2, this.a.getFPC());
/* 408:373 */         this.a.setFPC(addr);
/* 409:374 */         break;
/* 410:    */       default: 
/* 411:375 */         System.out.println("FMT2 OPC=" + opc + " EXT=" + ext);
/* 412:    */       }
/* 413:376 */       break;
/* 414:    */     default: 
/* 415:377 */       System.out.println("FMT2 OPC=" + opc);
/* 416:    */     }
/* 417:    */   }
/* 418:    */   
/* 419:    */   private void fmt3(int fxy)
/* 420:    */   {
/* 421:382 */     switch (fxy)
/* 422:    */     {
/* 423:    */     case 0: 
/* 424:384 */       this.a.store(this.APTP);
/* 425:385 */       this.FPRun = false;
/* 426:386 */       this.FPFlag = true;
/* 427:387 */       break;
/* 428:    */     case 1: 
/* 429:389 */       this.FPPause = true;
/* 430:390 */       break;
/* 431:    */     case 2: 
/* 432:392 */       this.f.m = 0L;
/* 433:393 */       this.f.e = 0;
/* 434:394 */       break;
/* 435:    */     case 3: 
/* 436:396 */       this.f.m = (-this.f.m);
/* 437:397 */       break;
/* 438:    */     case 4: 
/* 439:399 */       this.f.norm();
/* 440:    */       
/* 441:    */ 
/* 442:    */ 
/* 443:403 */       break;
/* 444:    */     case 5: 
/* 445:405 */       System.out.println("STARTF");
/* 446:406 */       break;
/* 447:    */     case 6: 
/* 448:408 */       System.out.println("STARTD");
/* 449:409 */       break;
/* 450:    */     case 7: 
/* 451:411 */       this.a.setFPC((int)(this.f.m >> 40));
/* 452:412 */       break;
/* 453:    */     default: 
/* 454:414 */       System.out.println("FMT3 FXY=" + fxy);
/* 455:    */     }
/* 456:    */   }
/* 457:    */   
/* 458:    */   private void fmt3x(int ext, int fxy)
/* 459:    */   {
/* 460:    */     int tmp;
/* 461:    */     int diff;
/* 462:421 */     switch (ext)
/* 463:    */     {
/* 464:    */     case 1: 
/* 465:423 */       if (fxy != 0)
/* 466:    */       {
/* 467:424 */         int tmp = this.a.readIndex(fxy);
/* 468:425 */         tmp = tmp << 20 >> 20;
/* 469:    */       }
/* 470:    */       else
/* 471:    */       {
/* 472:426 */         tmp = 23;
/* 473:    */       }
/* 474:427 */       if (this.mode != Mode.DP)
/* 475:    */       {
/* 476:428 */         int diff = tmp - this.f.e;
/* 477:429 */         this.f.e = tmp;
/* 478:    */       }
/* 479:    */       else
/* 480:    */       {
/* 481:430 */         diff = tmp;
/* 482:    */       }
/* 483:431 */       this.f.shiftm(diff);
/* 484:432 */       break;
/* 485:    */     case 2: 
/* 486:434 */       this.f.copyTo(this.o);
/* 487:    */       int diff;
/* 488:435 */       if (this.mode != Mode.DP) {
/* 489:436 */         diff = 23 - this.o.e;
/* 490:    */       } else {
/* 491:437 */         diff = 0;
/* 492:    */       }
/* 493:438 */       this.o.shiftm(diff);
/* 494:439 */       this.a.writeIndex(fxy, (int)(this.o.m >> 40));
/* 495:440 */       System.out.println("ATX");
/* 496:441 */       break;
/* 497:    */     case 3: 
/* 498:443 */       tmp = this.a.readIndex(fxy);
/* 499:444 */       this.f.m = (tmp << 52);
/* 500:445 */       this.f.m >>= 12;
/* 501:446 */       this.f.e = 23;
/* 502:447 */       this.f.norm();
/* 503:448 */       break;
/* 504:    */     case 4: 
/* 505:450 */       System.out.println("FNOP");
/* 506:451 */       break;
/* 507:    */     case 5: 
/* 508:453 */       System.out.println("STARTE");
/* 509:454 */       break;
/* 510:    */     default: 
/* 511:456 */       System.out.println("FMT3X EXT=" + ext + "FXY=" + fxy);
/* 512:    */     }
/* 513:    */   }
/* 514:    */   
/* 515:    */   private void oper(int addr, int opc)
/* 516:    */   {
/* 517:462 */     boolean sub = false;
/* 518:463 */     if (this.mode == Mode.DP) {
/* 519:464 */       System.out.println("OPE DP" + opc);
/* 520:    */     } else {
/* 521:466 */       switch (opc)
/* 522:    */       {
/* 523:    */       case 0: 
/* 524:468 */         this.o.load(addr);
/* 525:469 */         this.f.e = this.o.e;
/* 526:470 */         this.f.m = this.o.m;
/* 527:471 */         break;
/* 528:    */       case 2: 
/* 529:473 */         sub = true;
/* 530:    */       case 1: 
/* 531:476 */         this.o.load(addr);
/* 532:477 */         if (sub) {
/* 533:477 */           this.o.m = (-this.o.m);
/* 534:    */         }
/* 535:478 */         if (this.o.e > this.f.e)
/* 536:    */         {
/* 537:479 */           this.f.shiftm(this.o.e - this.f.e);
/* 538:480 */           this.f.e = this.o.e;
/* 539:    */         }
/* 540:    */         else
/* 541:    */         {
/* 542:482 */           this.o.shiftm(this.f.e - this.o.e);
/* 543:    */         }
/* 544:484 */         this.f.m = ((this.o.m >> 1) + (this.f.m >> 1));
/* 545:485 */         this.f.e += 1;
/* 546:486 */         this.f.norm();
/* 547:487 */         break;
/* 548:    */       case 3: 
/* 549:489 */         this.f.norm();
/* 550:490 */         this.f.f = (this.f.m * Math.pow(2.0D, -63.0D));
/* 551:491 */         this.o.load(addr);
/* 552:492 */         this.o.norm();
/* 553:493 */         this.o.f = (this.o.m * Math.pow(2.0D, -63.0D));
/* 554:494 */         this.f.f /= this.o.f;
/* 555:495 */         long test = Double.doubleToRawLongBits(this.f.f);
/* 556:496 */         this.f.m = (Math.round(this.f.f * Math.pow(2.0D, 63.0D)) & this.mode.mask());
/* 557:497 */         this.f.norm();
/* 558:498 */         this.f.e -= this.o.e;
/* 559:499 */         break;
/* 560:    */       case 4: 
/* 561:501 */         this.o.load(addr);
/* 562:502 */         if ((this.o.m > 0L) && (this.o.m <= this.mode.lobit()))
/* 563:    */         {
/* 564:503 */           this.o.m = 0L;
/* 565:504 */           this.o.e = 0;
/* 566:505 */           this.o.f = (this.o.m * Math.pow(2.0D, -63.0D));
/* 567:    */         }
/* 568:507 */         this.f.f *= this.o.f;
/* 569:508 */         this.f.m = ((this.f.f * Math.pow(2.0D, 63.0D)));
/* 570:509 */         this.f.norm();
/* 571:510 */         this.f.e += this.o.e;
/* 572:    */         
/* 573:    */ 
/* 574:    */ 
/* 575:    */ 
/* 576:    */ 
/* 577:    */ 
/* 578:    */ 
/* 579:    */ 
/* 580:    */ 
/* 581:    */ 
/* 582:    */ 
/* 583:    */ 
/* 584:    */ 
/* 585:524 */         break;
/* 586:    */       case 5: 
/* 587:526 */         this.o.load(addr);
/* 588:527 */         this.o.f = (this.f.f * this.o.f);
/* 589:528 */         this.o.m = ((this.o.f * Math.pow(2.0D, 63.0D)));
/* 590:529 */         this.o.e = (this.f.e + this.o.e);
/* 591:530 */         this.o.norm();
/* 592:531 */         this.o.store(addr);
/* 593:532 */         break;
/* 594:    */       case 6: 
/* 595:534 */         this.f.m = ((this.f.f * Math.pow(2.0D, 63.0D)));
/* 596:535 */         this.f.norm();
/* 597:536 */         this.f.store(addr);
/* 598:537 */         break;
/* 599:    */       case 7: 
/* 600:539 */         this.o.load(addr);
/* 601:540 */         this.o.f = (this.f.f / this.o.f);
/* 602:541 */         this.o.m = ((this.o.f * Math.pow(2.0D, 63.0D)));
/* 603:542 */         this.o.e = (this.f.e + this.o.e);
/* 604:543 */         this.o.norm();
/* 605:544 */         this.o.store(addr);
/* 606:545 */         break;
/* 607:    */       default: 
/* 608:546 */         System.out.println("OPE " + opc);
/* 609:    */       }
/* 610:    */     }
/* 611:    */   }
/* 612:    */   
/* 613:    */   private boolean cond(int cond)
/* 614:    */   {
/* 615:552 */     switch (cond)
/* 616:    */     {
/* 617:    */     case 0: 
/* 618:553 */       return this.f.m == 0L;
/* 619:    */     case 1: 
/* 620:554 */       return this.f.m >= 0L;
/* 621:    */     case 2: 
/* 622:555 */       return this.f.m <= 0L;
/* 623:    */     case 3: 
/* 624:556 */       return true;
/* 625:    */     case 4: 
/* 626:557 */       return this.f.m != 0L;
/* 627:    */     case 5: 
/* 628:558 */       return this.f.m < 0L;
/* 629:    */     case 6: 
/* 630:559 */       return this.f.m > 0L;
/* 631:    */     case 7: 
/* 632:560 */       return this.f.e > 23;
/* 633:    */     }
/* 634:562 */     return false;
/* 635:    */   }
/* 636:    */   
/* 637:    */   class APT
/* 638:    */   {
/* 639:    */     private int[] reg;
/* 640:    */     
/* 641:    */     public APT()
/* 642:    */     {
/* 643:568 */       this.reg = new int[] { 0, 0, 0, 0, 0 };
/* 644:    */     }
/* 645:    */     
/* 646:    */     public int getInstr()
/* 647:    */     {
/* 648:575 */       int addr = getFPC();
/* 649:576 */       int instr = FPPOld.this.data.memory[addr] & 0xFFF;
/* 650:    */       
/* 651:578 */       setFPC(addr + 1);
/* 652:579 */       return instr;
/* 653:    */     }
/* 654:    */     
/* 655:    */     public int read(int addr)
/* 656:    */     {
/* 657:582 */       setOpadd(addr);
/* 658:583 */       return FPPOld.this.data.memory[(addr & 0x7FFF)] & 0xFFF;
/* 659:    */     }
/* 660:    */     
/* 661:    */     public void write(int addr, int word)
/* 662:    */     {
/* 663:586 */       setOpadd(addr);
/* 664:587 */       FPPOld.this.data.memory[(addr & 0x7FFF)] = (word & 0xFFF);
/* 665:    */     }
/* 666:    */     
/* 667:    */     public int getFPC()
/* 668:    */     {
/* 669:590 */       return this.reg[1] & 0xFFF | (this.reg[0] & 0x7) << 12;
/* 670:    */     }
/* 671:    */     
/* 672:    */     public void setFPC(int fpc)
/* 673:    */     {
/* 674:593 */       this.reg[0] |= fpc >> 12 & 0x7;
/* 675:594 */       this.reg[1] = (fpc & 0xFFF);
/* 676:    */     }
/* 677:    */     
/* 678:    */     public int readIndex(int xr)
/* 679:    */     {
/* 680:597 */       return read(getIndex() + xr);
/* 681:    */     }
/* 682:    */     
/* 683:    */     public void writeIndex(int xr, int word)
/* 684:    */     {
/* 685:600 */       write(getIndex() + xr, word);
/* 686:    */     }
/* 687:    */     
/* 688:    */     public int getIndex()
/* 689:    */     {
/* 690:603 */       return this.reg[2] & 0xFFF | (this.reg[0] & 0x38) << 9;
/* 691:    */     }
/* 692:    */     
/* 693:    */     public void setIndex(int ind)
/* 694:    */     {
/* 695:606 */       this.reg[0] |= ind >> 9 & 0x38;
/* 696:607 */       this.reg[2] = (ind & 0xFFF);
/* 697:    */     }
/* 698:    */     
/* 699:    */     public int getBase()
/* 700:    */     {
/* 701:610 */       return this.reg[3] & 0xFFF | (this.reg[0] & 0x1C0) << 6;
/* 702:    */     }
/* 703:    */     
/* 704:    */     public void setBase(int base)
/* 705:    */     {
/* 706:613 */       this.reg[0] |= base >> 6 & 0x1C0;
/* 707:614 */       this.reg[3] = (base & 0xFFF);
/* 708:    */     }
/* 709:    */     
/* 710:    */     public int getOpadd()
/* 711:    */     {
/* 712:617 */       return this.reg[4] & 0xFFF | (this.reg[0] & 0xE00) << 3;
/* 713:    */     }
/* 714:    */     
/* 715:    */     public void setOpadd(int opadd)
/* 716:    */     {
/* 717:620 */       this.reg[0] |= opadd >> 3 & 0xE00;
/* 718:621 */       this.reg[4] = (opadd & 0xFFF);
/* 719:    */     }
/* 720:    */     
/* 721:    */     public void load(int addr)
/* 722:    */     {
/* 723:624 */       System.arraycopy(FPPOld.this.data.memory, addr, this.reg, 0, this.reg.length);
/* 724:    */       int xaddr;
/* 725:625 */       if (getFPC() >= 2912) {
/* 726:626 */         xaddr = 0;
/* 727:    */       }
/* 728:628 */       FPPOld.this.f.load(addr + this.reg.length);
/* 729:    */     }
/* 730:    */     
/* 731:    */     public void store(int addr)
/* 732:    */     {
/* 733:631 */       System.arraycopy(this.reg, 0, FPPOld.this.data.memory, addr, this.reg.length);
/* 734:632 */       FPPOld.this.f.store(addr + this.reg.length);
/* 735:    */     }
/* 736:    */   }
/* 737:    */   
/* 738:    */   class FAC
/* 739:    */   {
/* 740:    */     private int[] reg;
/* 741:    */     public double f;
/* 742:    */     public long m;
/* 743:    */     public int e;
/* 744:    */     public int es;
/* 745:642 */     public long one = 2305843009213693952L;
/* 746:    */     
/* 747:    */     public FAC()
/* 748:    */     {
/* 749:645 */       this.reg = new int[] { 0, 0, 0, 0, 0, 0 };
/* 750:    */     }
/* 751:    */     
/* 752:    */     public void load(int addr)
/* 753:    */     {
/* 754:654 */       FPPOld.this.a.setOpadd(addr + 2);
/* 755:    */       
/* 756:    */ 
/* 757:    */ 
/* 758:    */ 
/* 759:    */ 
/* 760:    */ 
/* 761:    */ 
/* 762:    */ 
/* 763:663 */       this.reg[0] = FPPOld.this.data.memory[(addr + 0)];
/* 764:664 */       this.e = (this.reg[0] << 20 >> 20);
/* 765:665 */       this.m = 0L;
/* 766:666 */       this.reg[1] = FPPOld.this.data.memory[(addr + 1)];
/* 767:667 */       this.m |= (this.reg[1] & 0xFFF) << 52;
/* 768:668 */       this.reg[2] = FPPOld.this.data.memory[(addr + 2)];
/* 769:669 */       this.m |= (this.reg[2] & 0xFFF) << 40;
/* 770:670 */       this.reg[3] = FPPOld.this.data.memory[(addr + 3)];
/* 771:671 */       this.m |= (this.reg[3] & 0xFFF) << 28;
/* 772:672 */       this.reg[4] = FPPOld.this.data.memory[(addr + 4)];
/* 773:673 */       this.m |= (this.reg[4] & 0xFFF) << 16;
/* 774:674 */       this.reg[5] = FPPOld.this.data.memory[(addr + 5)];
/* 775:675 */       this.m |= (this.reg[5] & 0xFFF) << 4;
/* 776:676 */       this.f = (this.m * Math.pow(2.0D, -63.0D));
/* 777:677 */       double test1 = mtof(this.m);
/* 778:    */     }
/* 779:    */     
/* 780:    */     public void store(int addr)
/* 781:    */     {
/* 782:681 */       FPPOld.this.a.setOpadd(addr + 2);
/* 783:682 */       save();
/* 784:    */       
/* 785:    */ 
/* 786:    */ 
/* 787:    */ 
/* 788:    */ 
/* 789:    */ 
/* 790:689 */       System.arraycopy(this.reg, 0, FPPOld.this.data.memory, addr, this.reg.length);
/* 791:    */     }
/* 792:    */     
/* 793:    */     public void copyTo(FAC x)
/* 794:    */     {
/* 795:693 */       System.arraycopy(this.reg, 0, x.reg, 0, this.reg.length);
/* 796:694 */       x.f = this.f;
/* 797:695 */       x.e = this.e;
/* 798:696 */       x.m = this.m;
/* 799:    */     }
/* 800:    */     
/* 801:    */     public void save()
/* 802:    */     {
/* 803:700 */       this.reg[0] = (this.e & 0xFFF);
/* 804:701 */       this.reg[1] = ((int)(this.m >>> 52 & 0xFFF));
/* 805:702 */       this.reg[2] = ((int)(this.m >>> 40 & 0xFFF));
/* 806:703 */       this.reg[3] = ((int)(this.m >>> 28 & 0xFFF));
/* 807:704 */       this.reg[4] = ((int)(this.m >>> 16 & 0xFFF));
/* 808:705 */       this.reg[5] = ((int)(this.m >>> 4 & 0xFFF));
/* 809:    */     }
/* 810:    */     
/* 811:    */     public void fix()
/* 812:    */     {
/* 813:710 */       this.e = (this.f == 0.0D ? 0 : Math.getExponent(this.f) + 1);
/* 814:711 */       this.m = (this.f == 0.0D ? 0L : (this.f * Math.pow(2.0D, 63.0D - this.e)));
/* 815:    */     }
/* 816:    */     
/* 817:    */     public void flo()
/* 818:    */     {
/* 819:715 */       this.f = (this.m * Math.pow(2.0D, this.e - 63.0D));
/* 820:    */     }
/* 821:    */     
/* 822:    */     public void norm()
/* 823:    */     {
/* 824:719 */       if (this.m == 0L)
/* 825:    */       {
/* 826:720 */         this.e = 0;
/* 827:721 */         return;
/* 828:    */       }
/* 829:723 */       if (this.m == 9223372036854775792L)
/* 830:    */       {
/* 831:724 */         this.m = (this.m + 16L >>> 1);
/* 832:725 */         this.e += 1;
/* 833:    */       }
/* 834:727 */       while (((this.m ^ this.m << 1) & FPPOld.hibit) == 0L)
/* 835:    */       {
/* 836:728 */         this.m <<= 1;
/* 837:729 */         this.e -= 1;
/* 838:    */       }
/* 839:731 */       if (this.m << 1 == 0L)
/* 840:    */       {
/* 841:732 */         this.m >>= 1;
/* 842:733 */         this.e += 1;
/* 843:    */       }
/* 844:    */     }
/* 845:    */     
/* 846:    */     public void shiftm(int diff)
/* 847:    */     {
/* 848:739 */       int shift = Math.min(63, Math.abs(diff));
/* 849:740 */       if (diff > 0) {
/* 850:740 */         this.m >>= shift;
/* 851:    */       } else {
/* 852:741 */         this.m <<= shift;
/* 853:    */       }
/* 854:742 */       if (this.m == 0L) {
/* 855:742 */         this.e = 0;
/* 856:    */       }
/* 857:    */     }
/* 858:    */     
/* 859:    */     public double mtof(long m)
/* 860:    */     {
/* 861:    */       long arg;
/* 862:    */       long exp;
/* 863:747 */       if (m < 0L)
/* 864:    */       {
/* 865:748 */         long exp = -4620693217682128896L;
/* 866:749 */         arg = -m << 2;
/* 867:    */       }
/* 868:    */       else
/* 869:    */       {
/* 870:751 */         exp = 4602678819172646912L;
/* 871:752 */         arg = m << 2;
/* 872:    */       }
/* 873:754 */       arg >>>= 12;
/* 874:755 */       long arg = exp | arg;
/* 875:756 */       return Double.longBitsToDouble(arg);
/* 876:    */     }
/* 877:    */   }
/* 878:    */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Logic.FPPOld
 * JD-Core Version:    0.7.0.1
 */