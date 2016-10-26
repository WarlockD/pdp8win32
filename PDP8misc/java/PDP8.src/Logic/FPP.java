/*   1:    */ package Logic;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Arrays;
/*   5:    */ 
/*   6:    */ public class FPP
/*   7:    */   implements Device, Constants
/*   8:    */ {
/*   9: 21 */   public static int DevId55 = 45;
/*  10: 22 */   public static int DevId56 = 46;
/*  11:    */   public BusRegMem data;
/*  12: 26 */   private final Object lock = new Object();
/*  13: 28 */   private boolean FPPFlag = false;
/*  14: 29 */   private boolean FPPRun = false;
/*  15: 30 */   private boolean FPPPaus = false;
/*  16: 31 */   private boolean FPPStep = false;
/*  17: 32 */   private boolean FPPintena = false;
/*  18: 33 */   private boolean FPPdebug = true;
/*  19: 34 */   private static long hibit = -9223372036854775808L;
/*  20:    */   private Mode mode;
/*  21:    */   
/*  22:    */   private static enum Mode
/*  23:    */   {
/*  24: 36 */     DP(2, 1, 1099511627776L, -1099511627776L),  FP(3, 0, 1099511627776L, -1099511627776L),  EP(6, 0, 16L, -16L);
/*  25:    */     
/*  26:    */     private int val;
/*  27:    */     private int offs;
/*  28:    */     private long lobit;
/*  29:    */     private long mask;
/*  30:    */     
/*  31:    */     private Mode(int val, int offs, long lobit, long mask)
/*  32:    */     {
/*  33: 44 */       this.val = val;
/*  34: 45 */       this.offs = offs;
/*  35: 46 */       this.lobit = lobit;
/*  36: 47 */       this.mask = mask;
/*  37:    */     }
/*  38:    */     
/*  39:    */     public int val()
/*  40:    */     {
/*  41: 50 */       return this.val;
/*  42:    */     }
/*  43:    */     
/*  44:    */     public int offs()
/*  45:    */     {
/*  46: 53 */       return this.offs;
/*  47:    */     }
/*  48:    */     
/*  49:    */     public long lobit()
/*  50:    */     {
/*  51: 56 */       return this.lobit;
/*  52:    */     }
/*  53:    */     
/*  54:    */     public long mask()
/*  55:    */     {
/*  56: 59 */       return this.mask;
/*  57:    */     }
/*  58:    */   }
/*  59:    */   
/*  60:    */   private static enum Stat
/*  61:    */   {
/*  62: 63 */     trapExit(1024),  hltExit(512),  divZero(256),  DPOverflow(128),  expOverflow(64),  expUnderflow(32),  FMInstr(16),  lockout(8),  FPPPaus(2),  FPPRun(1),  none(0);
/*  63:    */     
/*  64:    */     private int val;
/*  65:    */     
/*  66:    */     private Stat(int val)
/*  67:    */     {
/*  68: 76 */       this.val = val;
/*  69:    */     }
/*  70:    */     
/*  71:    */     public int val()
/*  72:    */     {
/*  73: 79 */       return this.val;
/*  74:    */     }
/*  75:    */   }
/*  76:    */   
/*  77: 83 */   private boolean underExit = false;
/*  78: 84 */   private boolean APT4K = false;
/*  79: 85 */   private boolean APTFast = false;
/*  80: 86 */   private boolean lockout = false;
/*  81: 87 */   private boolean trapExit = false;
/*  82: 88 */   private boolean hltExit = false;
/*  83: 89 */   private boolean divZero = false;
/*  84: 90 */   private boolean DPOverflow = false;
/*  85: 91 */   private boolean expOverflow = false;
/*  86: 92 */   private boolean expUnderflow = false;
/*  87: 93 */   private boolean FMInstr = false;
/*  88: 94 */   private boolean APTDump = true;
/*  89:    */   private Thread FPPThread;
/*  90:    */   private int APTP;
/*  91:    */   private APT a;
/*  92:    */   private APT s;
/*  93:    */   private FAC f;
/*  94:    */   private FAC o;
/*  95:    */   private FAC t;
/*  96:    */   
/*  97:    */   public FPP(BusRegMem data)
/*  98:    */   {
/*  99:134 */     this.data = data;
/* 100:135 */     this.a = new APT();
/* 101:136 */     this.s = new APT();
/* 102:137 */     this.f = new FAC();
/* 103:138 */     this.o = new FAC();
/* 104:139 */     this.t = new FAC();
/* 105:140 */     this.FPPThread = new Thread(new FPProc(), "FPP Thread");
/* 106:    */     
/* 107:142 */     this.FPPThread.start();
/* 108:    */   }
/* 109:    */   
/* 110:    */   public void Decode(int devcode, int opcode)
/* 111:    */   {
/* 112:147 */     if (devcode == DevId55) {
/* 113:148 */       switch (opcode)
/* 114:    */       {
/* 115:    */       case 1: 
/* 116:    */         break;
/* 117:    */       case 2: 
/* 118:    */         break;
/* 119:    */       case 3: 
/* 120:153 */         this.data.c0c1c2 = 0;
/* 121:154 */         break;
/* 122:    */       case 4: 
/* 123:    */         break;
/* 124:    */       case 5: 
/* 125:157 */         this.data.c0c1c2 = 0;
/* 126:158 */         break;
/* 127:    */       case 6: 
/* 128:159 */         this.data.c0c1c2 = 3;
/* 129:160 */         break;
/* 130:    */       case 7: 
/* 131:161 */         this.data.c0c1c2 = 3;
/* 132:    */       }
/* 133:    */     }
/* 134:165 */     if (devcode == DevId56) {
/* 135:166 */       switch (opcode)
/* 136:    */       {
/* 137:    */       case 7: 
/* 138:167 */         this.data.c0c1c2 = 1;
/* 139:    */       }
/* 140:    */     }
/* 141:    */   }
/* 142:    */   
/* 143:    */   public void Execute(int devcode, int opcode)
/* 144:    */   {
/* 145:174 */     if (devcode == DevId55) {
/* 146:175 */       switch (opcode)
/* 147:    */       {
/* 148:    */       case 1: 
/* 149:176 */         this.data.skipbus = this.FPPFlag;
/* 150:177 */         break;
/* 151:    */       case 2: 
/* 152:178 */         ClearFlags(DevId55);
/* 153:179 */         break;
/* 154:    */       case 3: 
/* 155:180 */         setCommand(this.data.data);
/* 156:181 */         break;
/* 157:    */       case 4: 
/* 158:182 */         setHalt();
/* 159:183 */         break;
/* 160:    */       case 5: 
/* 161:184 */         this.data.skipbus = setStart(this.data.data);
/* 162:185 */         break;
/* 163:    */       case 6: 
/* 164:186 */         this.data.data = getStatus();
/* 165:187 */         break;
/* 166:    */       case 7: 
/* 167:188 */         this.data.skipbus = this.FPPFlag;
/* 168:188 */         if (this.FPPFlag)
/* 169:    */         {
/* 170:189 */           this.data.data = getStatus();clearStatus();this.FPPFlag = false;clearIntReq();
/* 171:    */         }
/* 172:    */         break;
/* 173:    */       }
/* 174:    */     }
/* 175:193 */     if (devcode == DevId56) {
/* 176:194 */       switch (opcode)
/* 177:    */       {
/* 178:    */       case 7: 
/* 179:196 */         this.mode = (((!this.FPPRun ? 1 : 0) & ((this.data.data & 0x800) > 0 ? 1 : 0)) != 0 ? Mode.EP : this.mode);
/* 180:    */       }
/* 181:    */     }
/* 182:    */   }
/* 183:    */   
/* 184:    */   private void setCommand(int command)
/* 185:    */   {
/* 186:203 */     if (((!this.FPPRun ? 1 : 0) & (!this.FPPFlag ? 1 : 0)) != 0)
/* 187:    */     {
/* 188:204 */       this.mode = ((command & 0x800) > 0 ? Mode.DP : Mode.FP);
/* 189:205 */       this.underExit = ((command & 0x400) > 0);
/* 190:206 */       this.APT4K = ((command & 0x200) > 0);
/* 191:207 */       this.FPPintena = ((command & 0x100) > 0);
/* 192:208 */       this.APTFast = ((command & 0xF0) == 240);
/* 193:209 */       this.lockout = ((command & 0x8) > 0);
/* 194:210 */       this.APTP = ((command & 0x7) << 12);
/* 195:    */     }
/* 196:    */   }
/* 197:    */   
/* 198:    */   private void setStatus(Stat stat)
/* 199:    */   {
/* 200:215 */     switch (1.$SwitchMap$Logic$FPP$Stat[stat.ordinal()])
/* 201:    */     {
/* 202:    */     case 1: 
/* 203:216 */       this.trapExit = true; break;
/* 204:    */     case 2: 
/* 205:217 */       this.hltExit = true; break;
/* 206:    */     case 3: 
/* 207:218 */       this.divZero = true; break;
/* 208:    */     case 4: 
/* 209:219 */       this.DPOverflow = true; break;
/* 210:    */     case 5: 
/* 211:220 */       this.expOverflow = true; break;
/* 212:    */     case 6: 
/* 213:221 */       this.expUnderflow = true; break;
/* 214:    */     case 7: 
/* 215:222 */       this.FMInstr = true; break;
/* 216:    */     case 8: 
/* 217:223 */       this.lockout = true;
/* 218:    */     }
/* 219:    */   }
/* 220:    */   
/* 221:    */   private void clearStatus()
/* 222:    */   {
/* 223:228 */     this.trapExit = false;
/* 224:229 */     this.hltExit = false;
/* 225:230 */     this.divZero = false;
/* 226:231 */     this.DPOverflow = false;
/* 227:232 */     this.expOverflow = false;
/* 228:233 */     this.expUnderflow = false;
/* 229:234 */     this.FMInstr = false;
/* 230:235 */     this.lockout = false;
/* 231:    */   }
/* 232:    */   
/* 233:    */   private int getCommand()
/* 234:    */   {
/* 235:239 */     int command = 0;
/* 236:240 */     command |= (this.mode == Mode.DP ? 2048 : 0);
/* 237:241 */     command |= (this.underExit ? 1024 : 0);
/* 238:242 */     command |= (this.APT4K ? 512 : 0);
/* 239:243 */     command |= (this.FPPintena ? 256 : 0);
/* 240:244 */     command |= (this.APTFast ? 240 : 0);
/* 241:245 */     command |= (this.lockout ? 8 : 0);
/* 242:246 */     command |= this.APTP >>> 12 & 0x7;
/* 243:247 */     return command;
/* 244:    */   }
/* 245:    */   
/* 246:    */   private int getStatus()
/* 247:    */   {
/* 248:251 */     int statreg = 0;
/* 249:252 */     statreg |= (this.mode == Mode.DP ? 2048 : 0);
/* 250:253 */     statreg |= (this.trapExit ? Stat.trapExit.val : 0);
/* 251:254 */     statreg |= (this.hltExit ? Stat.hltExit.val : 0);
/* 252:255 */     statreg |= (this.divZero ? Stat.divZero.val : 0);
/* 253:256 */     statreg |= (this.DPOverflow ? Stat.DPOverflow.val : 0);
/* 254:257 */     statreg |= (this.expOverflow ? Stat.expOverflow.val : 0);
/* 255:258 */     statreg |= (this.expUnderflow ? Stat.expUnderflow.val : 0);
/* 256:259 */     statreg |= (this.FMInstr ? Stat.FMInstr.val : 0);
/* 257:260 */     statreg |= (this.lockout ? Stat.lockout.val : 0);
/* 258:261 */     statreg |= (this.mode == Mode.EP ? 4 : 0);
/* 259:262 */     statreg |= (this.FPPPaus ? Stat.FPPPaus.val : 0);
/* 260:263 */     statreg |= (this.FPPRun ? Stat.FPPRun.val : 0);
/* 261:    */     
/* 262:265 */     return statreg;
/* 263:    */   }
/* 264:    */   
/* 265:    */   private boolean setStart(int aptp)
/* 266:    */   {
/* 267:269 */     if ((this.FPPRun & this.FPPPaus))
/* 268:    */     {
/* 269:270 */       this.FPPPaus = false;
/* 270:271 */       return true;
/* 271:    */     }
/* 272:273 */     if (((!this.FPPRun ? 1 : 0) & (!this.FPPFlag ? 1 : 0)) != 0)
/* 273:    */     {
/* 274:274 */       this.APTP |= aptp & 0xFFF;
/* 275:275 */       this.a.load(this.APTP);
/* 276:276 */       this.a.setOpadd(this.a.getFPC());
/* 277:277 */       clearStatus();
/* 278:278 */       this.FPPRun = true;
/* 279:279 */       this.FPPPaus = false;
/* 280:280 */       this.APTDump = true;
/* 281:281 */       return true;
/* 282:    */     }
/* 283:283 */     return false;
/* 284:    */   }
/* 285:    */   
/* 286:    */   private void setHalt()
/* 287:    */   {
/* 288:288 */     if (this.FPPRun)
/* 289:    */     {
/* 290:290 */       if (this.FPPPaus) {
/* 291:291 */         this.a.setFPC(this.a.getFPC() - 1);
/* 292:    */       }
/* 293:    */     }
/* 294:    */     else
/* 295:    */     {
/* 296:294 */       this.FPPRun = true;
/* 297:295 */       this.FPPStep = true;
/* 298:    */     }
/* 299:297 */     setStatus(Stat.hltExit);
/* 300:298 */     Thread.yield();
/* 301:    */   }
/* 302:    */   
/* 303:    */   public void clearIntReq()
/* 304:    */   {
/* 305:303 */     if (!this.FPPintena) {
/* 306:303 */       this.data.setIntReq(DevId55, false);
/* 307:    */     }
/* 308:304 */     if (!this.FPPFlag) {
/* 309:305 */       this.data.setIntReq(DevId55, false);
/* 310:    */     }
/* 311:    */   }
/* 312:    */   
/* 313:    */   public void setIntReq()
/* 314:    */   {
/* 315:310 */     if (!this.FPPintena) {
/* 316:310 */       this.data.setIntReq(DevId55, false);
/* 317:    */     }
/* 318:311 */     if (this.FPPFlag == true) {
/* 319:312 */       this.data.setIntReq(DevId55, this.FPPintena);
/* 320:    */     }
/* 321:    */   }
/* 322:    */   
/* 323:    */   public void ClearFlags(int devcode)
/* 324:    */   {
/* 325:317 */     this.FPPFlag = false;
/* 326:318 */     this.FPPRun = false;
/* 327:319 */     this.FPPPaus = false;
/* 328:    */     
/* 329:321 */     clearIntReq();
/* 330:    */   }
/* 331:    */   
/* 332:    */   public void Interrupt(int command) {}
/* 333:    */   
/* 334:    */   public void ClearRun(boolean run) {}
/* 335:    */   
/* 336:    */   public void CloseDev(int devcode) {}
/* 337:    */   
/* 338:    */   public class FPProc
/* 339:    */     implements Runnable
/* 340:    */   {
/* 341:    */     int addr;
/* 342:    */     
/* 343:    */     public FPProc() {}
/* 344:    */     
/* 345:    */     public void run()
/* 346:    */     {
/* 347:    */       for (;;)
/* 348:    */       {
/* 349:337 */         if (!FPP.this.data.run) {
/* 350:337 */           FPP.this.FPPRun = false;
/* 351:    */         }
/* 352:338 */         while ((FPP.this.data.run & FPP.this.FPPRun & !FPP.this.FPPPaus))
/* 353:    */         {
/* 354:340 */           FPP.this.FMInstr = false;
/* 355:341 */           int ins = FPP.this.a.getInstr();
/* 356:342 */           int opc = (ins & 0xE00) >> 9;
/* 357:343 */           int mod = (ins & 0x180) >> 7;
/* 358:344 */           if (((FPP.this.mode != FPP.Mode.DP ? 1 : 0) & (opc == 7 ? 1 : 0) & (mod == 0 ? 1 : 0)) != 0)
/* 359:    */           {
/* 360:344 */             opc = 15;mod = 3;
/* 361:    */           }
/* 362:345 */           int ext = (ins & 0x78) >> 3;
/* 363:346 */           int xr = (ins & 0x38) >> 3;
/* 364:347 */           int fxy = ins & 0x7;
/* 365:348 */           int bas = ins & 0x7F;
/* 366:349 */           int inc = (ins & 0x40) >> 6;
/* 367:350 */           if (FPP.this.FPPdebug) {
/* 368:350 */             System.out.printf(" %2s  %04o ", new Object[] { FPP.this.mode, Integer.valueOf(ins) });
/* 369:    */           }
/* 370:351 */           if (((FPP.this.a.getFPC() - 1 == 6111 ? 1 : 0) & (ins == 329 ? 1 : 0)) != 0) {
/* 371:352 */             FPP.this.lockout = false;
/* 372:    */           }
/* 373:354 */           switch (mod)
/* 374:    */           {
/* 375:    */           case 0: 
/* 376:356 */             switch (opc)
/* 377:    */             {
/* 378:    */             case 0: 
/* 379:358 */               switch (ext)
/* 380:    */               {
/* 381:    */               case 0: 
/* 382:360 */                 FPP.this.fmt3(fxy); break;
/* 383:    */               case 1: 
/* 384:    */               case 2: 
/* 385:    */               case 3: 
/* 386:    */               case 4: 
/* 387:    */               case 5: 
/* 388:363 */                 FPP.this.fmt3x(ext, fxy); break;
/* 389:    */               case 8: 
/* 390:    */               case 9: 
/* 391:365 */                 FPP.this.fmt2(opc, ext, fxy); break;
/* 392:    */               case 6: 
/* 393:    */               case 7: 
/* 394:    */               default: 
/* 395:366 */                 if (FPP.this.FPPdebug) {
/* 396:366 */                   System.out.print("EXT " + ext);
/* 397:    */                 }
/* 398:    */                 break;
/* 399:    */               }
/* 400:    */               break;
/* 401:    */             case 1: 
/* 402:369 */               FPP.this.fmt2(opc, ext, fxy); break;
/* 403:    */             case 2: 
/* 404:371 */               this.addr = (FPP.this.a.getInstr() | fxy << 12);
/* 405:372 */               if (FPP.this.FPPdebug) {
/* 406:372 */                 System.out.printf(" AD %05o ", new Object[] { Integer.valueOf(this.addr) });
/* 407:    */               }
/* 408:373 */               if (FPP.this.FPPdebug) {
/* 409:373 */                 System.out.printf("JNX X=%01o:%04o", new Object[] { Integer.valueOf(xr), Integer.valueOf(FPP.this.a.readIndex(xr) + inc) });
/* 410:    */               }
/* 411:374 */               FPP.this.a.writeIndex(xr, FPP.this.a.readIndex(xr) + inc);
/* 412:375 */               if (FPP.this.a.readIndex(xr) > 0) {
/* 413:375 */                 FPP.this.a.setFPC(this.addr);
/* 414:    */               }
/* 415:    */               break;
/* 416:    */             case 3: 
/* 417:    */             case 4: 
/* 418:379 */               FPP.this.fmt2(opc, ext, fxy); break;
/* 419:    */             case 5: 
/* 420:381 */               if (FPP.this.FPPdebug) {
/* 421:381 */                 System.out.print("LTR");
/* 422:    */               }
/* 423:382 */               if (FPP.this.cond(ext))
/* 424:    */               {
/* 425:383 */                 FPP.this.f.m = FPP.this.f.one;
/* 426:384 */                 FPP.this.f.e = 1;
/* 427:    */               }
/* 428:    */               else
/* 429:    */               {
/* 430:386 */                 FPP.this.f.m = 0L;
/* 431:387 */                 FPP.this.f.e = 0;
/* 432:    */               }
/* 433:390 */               break;
/* 434:    */             default: 
/* 435:391 */               if (FPP.this.FPPdebug) {
/* 436:391 */                 System.out.print("OPC " + opc);
/* 437:    */               }
/* 438:    */               break;
/* 439:    */             }
/* 440:    */             break;
/* 441:    */           case 1: 
/* 442:394 */             this.addr = (FPP.this.a.getBase() + 3 * bas);
/* 443:395 */             FPP.this.oper(this.addr, opc, true);
/* 444:396 */             break;
/* 445:    */           case 2: 
/* 446:398 */             this.addr = (FPP.this.a.getInstr() | fxy << 12);
/* 447:399 */             if (FPP.this.FPPdebug) {
/* 448:399 */               System.out.printf(" AD %05o ", new Object[] { Integer.valueOf(this.addr) });
/* 449:    */             }
/* 450:400 */             FPP.this.a.writeIndex(xr, FPP.this.a.readIndex(xr) + inc);
/* 451:401 */             if (xr > 0) {
/* 452:401 */               this.addr += FPP.this.mode.val() * FPP.this.a.readIndex(xr);
/* 453:    */             }
/* 454:402 */             FPP.this.oper(this.addr, opc, false);
/* 455:403 */             break;
/* 456:    */           case 3: 
/* 457:405 */             FPP.this.o.load(FPP.this.a.getBase() + 3 * fxy, FPP.Mode.FP, false);
/* 458:406 */             FPP.this.a.writeIndex(xr, FPP.this.a.readIndex(xr) + inc);
/* 459:407 */             this.addr = ((int)(FPP.this.o.m >>> 40) & 0x7FFF);
/* 460:408 */             if (xr > 0) {
/* 461:408 */               this.addr += FPP.this.mode.val() * FPP.this.a.readIndex(xr);
/* 462:    */             }
/* 463:409 */             if (FPP.this.FPPdebug) {
/* 464:409 */               System.out.print("%");
/* 465:    */             }
/* 466:410 */             FPP.this.oper(this.addr, opc, false);
/* 467:411 */             break;
/* 468:    */           default: 
/* 469:412 */             if (FPP.this.FPPdebug) {
/* 470:412 */               System.out.print("MOD " + mod);
/* 471:    */             }
/* 472:    */             break;
/* 473:    */           }
/* 474:415 */           if (FPP.this.FPPStep)
/* 475:    */           {
/* 476:416 */             FPP.this.FPPRun = false;
/* 477:417 */             FPP.this.FPPStep = false;
/* 478:    */           }
/* 479:    */         }
/* 480:420 */         if (FPP.this.hltExit) {
/* 481:420 */           FPP.this.dump(FPP.Stat.divZero);
/* 482:    */         }
/* 483:    */         try
/* 484:    */         {
/* 485:422 */           Thread.sleep(10L);
/* 486:    */         }
/* 487:    */         catch (Exception e)
/* 488:    */         {
/* 489:424 */           if (FPP.this.FPPdebug) {
/* 490:424 */             System.out.println("FPProc=>" + e);
/* 491:    */           }
/* 492:    */         }
/* 493:    */       }
/* 494:    */     }
/* 495:    */   }
/* 496:    */   
/* 497:    */   private void fmt2(int opc, int ext, int fxy)
/* 498:    */   {
/* 499:431 */     int addr = 0;
/* 500:432 */     switch (opc)
/* 501:    */     {
/* 502:    */     case 0: 
/* 503:434 */       addr = this.a.getInstr();
/* 504:435 */       if (this.FPPdebug) {
/* 505:435 */         System.out.printf(" AD %05o ", new Object[] { Integer.valueOf(addr) });
/* 506:    */       }
/* 507:436 */       switch (ext)
/* 508:    */       {
/* 509:    */       case 8: 
/* 510:438 */         this.a.writeIndexOpa(fxy, addr);
/* 511:439 */         if (this.FPPdebug) {
/* 512:439 */           System.out.printf("LDX X=%01o:%04o", new Object[] { Integer.valueOf(fxy), Integer.valueOf(addr) });
/* 513:    */         }
/* 514:    */         break;
/* 515:    */       case 9: 
/* 516:442 */         addr = addr + this.a.readIndex(fxy) & 0xFFF;
/* 517:443 */         this.a.writeIndexOpa(fxy, addr);
/* 518:444 */         if (this.FPPdebug) {
/* 519:444 */           System.out.printf("ADX X=%01o:%04o", new Object[] { Integer.valueOf(fxy), Integer.valueOf(addr) });
/* 520:    */         }
/* 521:    */         break;
/* 522:    */       default: 
/* 523:446 */         if (this.FPPdebug) {
/* 524:446 */           System.out.print("FMT2 OPC=" + opc + " EXT=" + ext);
/* 525:    */         }
/* 526:    */         break;
/* 527:    */       }
/* 528:    */       break;
/* 529:    */     case 1: 
/* 530:449 */       addr = this.a.getInstr() | fxy << 12;
/* 531:450 */       if (this.FPPdebug) {
/* 532:450 */         System.out.printf(" AD %05o ", new Object[] { Integer.valueOf(addr) });
/* 533:    */       }
/* 534:451 */       switch (ext)
/* 535:    */       {
/* 536:    */       case 0: 
/* 537:    */       case 1: 
/* 538:    */       case 2: 
/* 539:    */       case 3: 
/* 540:    */       case 4: 
/* 541:    */       case 5: 
/* 542:    */       case 6: 
/* 543:    */       case 7: 
/* 544:454 */         if (this.FPPdebug) {
/* 545:454 */           System.out.printf("BR %05o", new Object[] { Integer.valueOf(addr) });
/* 546:    */         }
/* 547:455 */         if (cond(ext)) {
/* 548:455 */           this.a.setFPC(addr);
/* 549:    */         }
/* 550:    */         break;
/* 551:    */       case 8: 
/* 552:457 */         if (this.FPPdebug) {
/* 553:457 */           System.out.printf("SETX %05o ", new Object[] { Integer.valueOf(addr) });
/* 554:    */         }
/* 555:458 */         this.a.setIndex(addr); break;
/* 556:    */       case 9: 
/* 557:460 */         if (this.FPPdebug) {
/* 558:460 */           System.out.printf("SETB %05o ", new Object[] { Integer.valueOf(addr) });
/* 559:    */         }
/* 560:461 */         this.a.setBase(addr); break;
/* 561:    */       case 10: 
/* 562:463 */         if (this.FPPdebug) {
/* 563:463 */           System.out.printf("JSA %05o ", new Object[] { Integer.valueOf(addr + 2) });
/* 564:    */         }
/* 565:464 */         this.a.write(addr, 536 + (this.a.getFPC() >> 12));
/* 566:465 */         this.a.write(addr + 1, this.a.getFPC());
/* 567:466 */         this.a.setFPC(addr + 2);
/* 568:467 */         this.a.setOpadd(addr + 1);
/* 569:468 */         break;
/* 570:    */       case 11: 
/* 571:470 */         if (this.FPPdebug) {
/* 572:470 */           System.out.printf("JSR %05o ", new Object[] { Integer.valueOf(addr) });
/* 573:    */         }
/* 574:471 */         this.a.write(this.a.getBase() + 1, 536 + (this.a.getFPC() >> 12));
/* 575:472 */         this.a.write(this.a.getBase() + 2, this.a.getFPC());
/* 576:473 */         this.a.setFPC(addr);
/* 577:474 */         this.a.setOpadd(addr);
/* 578:475 */         break;
/* 579:    */       default: 
/* 580:476 */         if (this.FPPdebug) {
/* 581:476 */           System.out.print("FMT2 OPC=" + opc + " EXT=" + ext);
/* 582:    */         }
/* 583:    */         break;
/* 584:    */       }
/* 585:    */       break;
/* 586:    */     case 3: 
/* 587:    */     case 4: 
/* 588:480 */       addr = this.a.getInstr() | fxy << 12;
/* 589:481 */       if (this.FPPdebug) {
/* 590:481 */         System.out.printf(" AD %05o ", new Object[] { Integer.valueOf(addr) });
/* 591:    */       }
/* 592:482 */       if (this.FPPdebug) {
/* 593:482 */         System.out.print("TRAP");
/* 594:    */       }
/* 595:483 */       this.a.setOpadd(addr);
/* 596:484 */       dump(Stat.trapExit); break;
/* 597:    */     case 2: 
/* 598:    */     default: 
/* 599:485 */       if (this.FPPdebug) {
/* 600:485 */         System.out.print("FMT2 OPC=" + opc);
/* 601:    */       }
/* 602:    */       break;
/* 603:    */     }
/* 604:    */   }
/* 605:    */   
/* 606:    */   private void fmt3(int fxy)
/* 607:    */   {
/* 608:490 */     switch (fxy)
/* 609:    */     {
/* 610:    */     case 0: 
/* 611:492 */       if (this.FPPdebug) {
/* 612:492 */         System.out.print("FEXIT");
/* 613:    */       }
/* 614:493 */       if (this.APTDump) {
/* 615:493 */         dump(Stat.none);
/* 616:    */       }
/* 617:    */       break;
/* 618:    */     case 1: 
/* 619:499 */       if (this.FPPdebug) {
/* 620:499 */         System.out.print("FPAUSE");
/* 621:    */       }
/* 622:500 */       this.FPPPaus = true;
/* 623:501 */       break;
/* 624:    */     case 2: 
/* 625:503 */       if (this.FPPdebug) {
/* 626:503 */         System.out.print("FCLA");
/* 627:    */       }
/* 628:504 */       this.f.m = 0L;
/* 629:505 */       this.f.e = 0;
/* 630:506 */       this.f.s = 0;
/* 631:507 */       break;
/* 632:    */     case 3: 
/* 633:509 */       if (this.FPPdebug) {
/* 634:509 */         System.out.print("FNEG");
/* 635:    */       }
/* 636:510 */       this.f.m = (-this.f.m);
/* 637:511 */       this.f.s = (-this.f.s);
/* 638:512 */       break;
/* 639:    */     case 4: 
/* 640:514 */       if (this.FPPdebug) {
/* 641:514 */         System.out.print("FNORM");
/* 642:    */       }
/* 643:515 */       if (this.mode != Mode.DP) {
/* 644:515 */         this.f.mnorm();
/* 645:    */       }
/* 646:    */       break;
/* 647:    */     case 5: 
/* 648:518 */       if (this.FPPdebug) {
/* 649:518 */         System.out.print("STARTF");
/* 650:    */       }
/* 651:519 */       if (this.mode == Mode.EP) {
/* 652:519 */         this.f.m &= Mode.FP.mask;
/* 653:    */       }
/* 654:520 */       this.mode = Mode.FP;
/* 655:521 */       break;
/* 656:    */     case 6: 
/* 657:523 */       if (this.FPPdebug) {
/* 658:523 */         System.out.print("STARTD");
/* 659:    */       }
/* 660:524 */       if (this.mode == Mode.EP) {
/* 661:524 */         this.f.m &= Mode.DP.mask;
/* 662:    */       }
/* 663:525 */       this.mode = Mode.DP;
/* 664:526 */       break;
/* 665:    */     case 7: 
/* 666:528 */       if (this.FPPdebug) {
/* 667:528 */         System.out.print("JAC");
/* 668:    */       }
/* 669:529 */       this.a.setFPC((int)(this.f.m >> 40));
/* 670:530 */       break;
/* 671:    */     default: 
/* 672:532 */       if (this.FPPdebug) {
/* 673:532 */         System.out.print("FMT3 FXY=" + fxy);
/* 674:    */       }
/* 675:    */       break;
/* 676:    */     }
/* 677:    */   }
/* 678:    */   
/* 679:    */   private void fmt3x(int ext, int fxy)
/* 680:    */   {
/* 681:    */     int tmp;
/* 682:539 */     switch (ext)
/* 683:    */     {
/* 684:    */     case 1: 
/* 685:542 */       if (fxy != 0)
/* 686:    */       {
/* 687:543 */         int tmp = this.a.readIndexOpa(fxy);
/* 688:544 */         tmp = tmp << 20 >> 20;
/* 689:    */       }
/* 690:    */       else
/* 691:    */       {
/* 692:545 */         tmp = 23;
/* 693:    */       }
/* 694:546 */       if (this.FPPdebug) {
/* 695:546 */         System.out.printf("ALN X=%01o:%04o", new Object[] { Integer.valueOf(fxy), Integer.valueOf(tmp & 0xFFF) });
/* 696:    */       }
/* 697:    */       int diff;
/* 698:547 */       if (this.mode != Mode.DP)
/* 699:    */       {
/* 700:548 */         int diff = tmp - this.f.e;
/* 701:549 */         this.f.e = tmp;
/* 702:    */       }
/* 703:    */       else
/* 704:    */       {
/* 705:550 */         diff = tmp;
/* 706:    */       }
/* 707:551 */       this.f.mshift(diff);
/* 708:552 */       break;
/* 709:    */     case 2: 
/* 710:555 */       this.f.copyTo(this.t);
/* 711:559 */       if (this.mode != Mode.DP) {
/* 712:559 */         this.t.mshift(23 - this.t.e);
/* 713:    */       }
/* 714:561 */       tmp = (int)(this.t.m >> 40);
/* 715:562 */       this.a.writeIndex(fxy, tmp);
/* 716:563 */       if (this.FPPdebug) {
/* 717:563 */         System.out.printf("ATX X=%01o:%04o", new Object[] { Integer.valueOf(fxy), Integer.valueOf(tmp & 0xFFF) });
/* 718:    */       }
/* 719:    */       break;
/* 720:    */     case 3: 
/* 721:566 */       tmp = this.a.readIndexOpa(fxy);
/* 722:567 */       if (this.FPPdebug) {
/* 723:567 */         System.out.printf("XTA X=%01o:%04o", new Object[] { Integer.valueOf(fxy), Integer.valueOf(tmp) });
/* 724:    */       }
/* 725:568 */       this.f.m = (tmp << 52);
/* 726:569 */       this.f.m >>= 12;
/* 727:570 */       this.f.e = 23;
/* 728:571 */       if (this.mode != Mode.DP) {
/* 729:571 */         this.f.mnorm();
/* 730:    */       }
/* 731:    */       break;
/* 732:    */     case 4: 
/* 733:574 */       if (this.FPPdebug) {
/* 734:574 */         System.out.print("FNOP");
/* 735:    */       }
/* 736:    */       break;
/* 737:    */     case 5: 
/* 738:577 */       if (this.FPPdebug) {
/* 739:577 */         System.out.print("STARTE");
/* 740:    */       }
/* 741:578 */       if (this.mode != Mode.EP) {
/* 742:578 */         this.f.m &= Mode.FP.mask;
/* 743:    */       }
/* 744:579 */       this.mode = Mode.EP;
/* 745:580 */       break;
/* 746:    */     default: 
/* 747:582 */       if (this.FPPdebug) {
/* 748:582 */         System.out.print("FMT3X EXT=" + ext + "FXY=" + fxy);
/* 749:    */       }
/* 750:    */       break;
/* 751:    */     }
/* 752:    */   }
/* 753:    */   
/* 754:    */   private void oper(int addr, int opc, boolean single)
/* 755:    */   {
/* 756:588 */     boolean sub = false;
/* 757:589 */     if (this.mode == Mode.DP) {
/* 758:590 */       switch (opc)
/* 759:    */       {
/* 760:    */       case 0: 
/* 761:592 */         if (this.FPPdebug) {
/* 762:592 */           System.out.printf("DP-FLDA %05o", new Object[] { Integer.valueOf(addr) });
/* 763:    */         }
/* 764:593 */         this.o.load(addr, this.mode, single);
/* 765:594 */         this.o.copyTo(this.f);
/* 766:595 */         break;
/* 767:    */       case 2: 
/* 768:597 */         sub = true;
/* 769:    */       case 1: 
/* 770:600 */         if ((this.FPPdebug & sub)) {
/* 771:600 */           System.out.printf("DP-FSUB %05o", new Object[] { Integer.valueOf(addr) });
/* 772:    */         }
/* 773:601 */         if ((this.FPPdebug & !sub)) {
/* 774:601 */           System.out.printf("DP-FADD %05o", new Object[] { Integer.valueOf(addr) });
/* 775:    */         }
/* 776:602 */         this.o.load(addr, this.mode, single);
/* 777:603 */         if (sub) {
/* 778:603 */           this.o.m = (-this.o.m);
/* 779:    */         }
/* 780:604 */         this.f.m += this.o.m;
/* 781:605 */         this.f.mround();
/* 782:606 */         break;
/* 783:    */       case 3: 
/* 784:608 */         if (this.FPPdebug) {
/* 785:608 */           System.out.printf("DP-FDIV %05o", new Object[] { Integer.valueOf(addr) });
/* 786:    */         }
/* 787:609 */         this.o.load(addr, this.mode, single);
/* 788:610 */         if (this.o.m == 0L)
/* 789:    */         {
/* 790:610 */           dump(Stat.divZero);
/* 791:    */         }
/* 792:    */         else
/* 793:    */         {
/* 794:611 */           if (this.o.s < 0) {
/* 795:611 */             this.o.m = (-this.o.m);
/* 796:    */           }
/* 797:612 */           if (this.f.s < 0) {
/* 798:612 */             this.f.m = (-this.f.m);
/* 799:    */           }
/* 800:613 */           this.f.mdiv(this.o);
/* 801:614 */           this.f.mround();
/* 802:    */         }
/* 803:615 */         break;
/* 804:    */       case 4: 
/* 805:617 */         if (this.FPPdebug) {
/* 806:617 */           System.out.printf("DP-FMUL %05o", new Object[] { Integer.valueOf(addr) });
/* 807:    */         }
/* 808:618 */         this.o.load(addr, this.mode, single);
/* 809:619 */         if (this.o.s < 0) {
/* 810:619 */           this.o.m = (-this.o.m);
/* 811:    */         }
/* 812:620 */         if (this.f.s < 0) {
/* 813:620 */           this.f.m = (-this.f.m);
/* 814:    */         }
/* 815:621 */         this.f.mmul(this.o);
/* 816:622 */         this.f.mround();
/* 817:623 */         break;
/* 818:    */       case 6: 
/* 819:625 */         if (this.FPPdebug) {
/* 820:625 */           System.out.printf("DP-FSTA %05o", new Object[] { Integer.valueOf(addr) });
/* 821:    */         }
/* 822:626 */         this.f.copyTo(this.o);
/* 823:627 */         this.o.store(addr, this.mode, single);
/* 824:628 */         break;
/* 825:    */       case 5: 
/* 826:    */       default: 
/* 827:629 */         if (!this.FPPdebug) {
/* 828:    */           break;
/* 829:    */         }
/* 830:629 */         System.out.print("OPE DP OPC=" + opc); break;
/* 831:    */       }
/* 832:    */     } else {
/* 833:632 */       switch (opc)
/* 834:    */       {
/* 835:    */       case 0: 
/* 836:634 */         if (this.FPPdebug) {
/* 837:634 */           System.out.printf("FLDA %05o", new Object[] { Integer.valueOf(addr) });
/* 838:    */         }
/* 839:635 */         this.o.load(addr, this.mode, single);
/* 840:636 */         this.o.copyTo(this.f);
/* 841:637 */         break;
/* 842:    */       case 2: 
/* 843:639 */         sub = true;
/* 844:    */       case 1: 
/* 845:642 */         if ((this.FPPdebug & sub)) {
/* 846:642 */           System.out.printf("FSUB %05o", new Object[] { Integer.valueOf(addr) });
/* 847:    */         }
/* 848:643 */         if ((this.FPPdebug & !sub)) {
/* 849:643 */           System.out.printf("FADD %05o", new Object[] { Integer.valueOf(addr) });
/* 850:    */         }
/* 851:644 */         this.o.load(addr, this.mode, single);
/* 852:645 */         if (sub) {
/* 853:645 */           this.o.m = (-this.o.m);
/* 854:    */         }
/* 855:646 */         this.f.madd(this.o);
/* 856:647 */         if (this.f.mnorm()) {
/* 857:647 */           this.f.mround();
/* 858:    */         }
/* 859:648 */         if (this.f.e > 2047) {
/* 860:649 */           dump(Stat.expOverflow);
/* 861:650 */         } else if (this.f.e < -2048) {
/* 862:651 */           if (this.underExit) {
/* 863:651 */             dump(Stat.expUnderflow);
/* 864:    */           } else {
/* 865:652 */             this.f.clear();
/* 866:    */           }
/* 867:    */         }
/* 868:    */         break;
/* 869:    */       case 3: 
/* 870:656 */         if (this.FPPdebug) {
/* 871:656 */           System.out.printf("FDIV %05o", new Object[] { Integer.valueOf(addr) });
/* 872:    */         }
/* 873:657 */         this.o.load(addr, this.mode, single);
/* 874:658 */         if (this.o.m == 0L)
/* 875:    */         {
/* 876:658 */           dump(Stat.divZero);
/* 877:    */         }
/* 878:    */         else
/* 879:    */         {
/* 880:659 */           if (this.o.s < 0) {
/* 881:659 */             this.o.m = (-this.o.m);
/* 882:    */           }
/* 883:660 */           this.o.mnorm();
/* 884:661 */           if (this.f.s < 0) {
/* 885:661 */             this.f.m = (-this.f.m);
/* 886:    */           }
/* 887:662 */           this.f.mnorm();
/* 888:663 */           this.f.mdiv(this.o);
/* 889:664 */           if (this.f.mnorm()) {
/* 890:664 */             this.f.mround();
/* 891:    */           }
/* 892:    */         }
/* 893:    */         break;
/* 894:    */       case 4: 
/* 895:667 */         if (this.FPPdebug) {
/* 896:667 */           System.out.printf("FMUL %05o", new Object[] { Integer.valueOf(addr) });
/* 897:    */         }
/* 898:668 */         this.o.load(addr, this.mode, single);
/* 899:669 */         if (this.o.m == Mode.EP.lobit)
/* 900:    */         {
/* 901:669 */           this.o.m = 0L;this.o.e = 0;
/* 902:    */         }
/* 903:670 */         this.o.mnorm();
/* 904:671 */         if (this.o.s < 0) {
/* 905:671 */           this.o.m = (-this.o.m);
/* 906:    */         }
/* 907:672 */         this.f.mnorm();
/* 908:673 */         if (this.f.s < 0) {
/* 909:673 */           this.f.m = (-this.f.m);
/* 910:    */         }
/* 911:674 */         this.f.mmul(this.o);
/* 912:675 */         if (this.f.mnorm()) {
/* 913:675 */           this.f.mround();
/* 914:    */         }
/* 915:    */         break;
/* 916:    */       case 5: 
/* 917:678 */         if (this.FPPdebug) {
/* 918:678 */           System.out.printf("FADDM %05o", new Object[] { Integer.valueOf(addr) });
/* 919:    */         }
/* 920:679 */         setStatus(Stat.FMInstr);
/* 921:680 */         this.o.load(addr, this.mode, single);
/* 922:681 */         this.f.copyTo(this.t);
/* 923:682 */         this.t.madd(this.o);
/* 924:683 */         if (this.t.mnorm()) {
/* 925:683 */           this.t.mround();
/* 926:    */         }
/* 927:684 */         this.t.store(addr, this.mode, single);
/* 928:685 */         break;
/* 929:    */       case 6: 
/* 930:687 */         if (this.FPPdebug) {
/* 931:687 */           System.out.printf("FSTA %05o", new Object[] { Integer.valueOf(addr) });
/* 932:    */         }
/* 933:688 */         this.f.copyTo(this.o);
/* 934:689 */         this.o.store(addr, this.mode, single);
/* 935:690 */         break;
/* 936:    */       case 7: 
/* 937:692 */         if (this.FPPdebug) {
/* 938:692 */           System.out.printf("FMULM %05o", new Object[] { Integer.valueOf(addr) });
/* 939:    */         }
/* 940:693 */         setStatus(Stat.FMInstr);
/* 941:694 */         this.o.load(addr, this.mode, single);
/* 942:695 */         this.f.copyTo(this.t);
/* 943:696 */         if (this.o.m == Mode.EP.lobit)
/* 944:    */         {
/* 945:696 */           this.o.m = 0L;this.o.e = 0;
/* 946:    */         }
/* 947:697 */         this.o.mnorm();
/* 948:698 */         if (this.o.s < 0) {
/* 949:698 */           this.o.m = (-this.o.m);
/* 950:    */         }
/* 951:699 */         this.t.mnorm();
/* 952:700 */         if (this.t.s < 0) {
/* 953:700 */           this.t.m = (-this.t.m);
/* 954:    */         }
/* 955:701 */         this.t.mmul(this.o);
/* 956:702 */         if (this.t.mnorm()) {
/* 957:702 */           this.t.mround();
/* 958:    */         }
/* 959:703 */         this.t.store(addr, this.mode, single);
/* 960:704 */         break;
/* 961:    */       case 15: 
/* 962:706 */         if (this.FPPdebug) {
/* 963:706 */           System.out.printf("LEAI %05o", new Object[] { Integer.valueOf(addr) });
/* 964:    */         }
/* 965:707 */         this.f.m = (addr << 40);
/* 966:708 */         this.f.e = 0;
/* 967:709 */         this.a.setOpadd(addr);
/* 968:710 */         this.mode = Mode.DP;
/* 969:711 */         break;
/* 970:    */       case 8: 
/* 971:    */       case 9: 
/* 972:    */       case 10: 
/* 973:    */       case 11: 
/* 974:    */       case 12: 
/* 975:    */       case 13: 
/* 976:    */       case 14: 
/* 977:    */       default: 
/* 978:712 */         System.out.print("OPE " + opc);
/* 979:    */       }
/* 980:    */     }
/* 981:    */   }
/* 982:    */   
/* 983:    */   private boolean cond(int cond)
/* 984:    */   {
/* 985:718 */     switch (cond)
/* 986:    */     {
/* 987:    */     case 0: 
/* 988:719 */       if (this.FPPdebug) {
/* 989:719 */         System.out.print(" JEQ");
/* 990:    */       }
/* 991:719 */       return this.f.m == 0L;
/* 992:    */     case 1: 
/* 993:720 */       if (this.FPPdebug) {
/* 994:720 */         System.out.print(" JGE");
/* 995:    */       }
/* 996:720 */       return this.f.m >= 0L;
/* 997:    */     case 2: 
/* 998:721 */       if (this.FPPdebug) {
/* 999:721 */         System.out.print(" JLE");
/* :00:    */       }
/* :01:721 */       return this.f.m <= 0L;
/* :02:    */     case 3: 
/* :03:722 */       if (this.FPPdebug) {
/* :04:722 */         System.out.print(" JA ");
/* :05:    */       }
/* :06:722 */       return true;
/* :07:    */     case 4: 
/* :08:723 */       if (this.FPPdebug) {
/* :09:723 */         System.out.print(" JNE");
/* :10:    */       }
/* :11:723 */       return this.f.m != 0L;
/* :12:    */     case 5: 
/* :13:724 */       if (this.FPPdebug) {
/* :14:724 */         System.out.print(" JLT");
/* :15:    */       }
/* :16:724 */       return this.f.m < 0L;
/* :17:    */     case 6: 
/* :18:725 */       if (this.FPPdebug) {
/* :19:725 */         System.out.print(" JGT");
/* :20:    */       }
/* :21:725 */       return this.f.m > 0L;
/* :22:    */     case 7: 
/* :23:726 */       if (this.FPPdebug) {
/* :24:726 */         System.out.print(" JAL");
/* :25:    */       }
/* :26:726 */       return this.f.e > 23;
/* :27:    */     }
/* :28:728 */     return false;
/* :29:    */   }
/* :30:    */   
/* :31:    */   public void dump(Stat x)
/* :32:    */   {
/* :33:734 */     if (this.APTDump) {
/* :34:734 */       this.a.store(this.APTP);
/* :35:    */     }
/* :36:737 */     setStatus(x);
/* :37:    */     
/* :38:739 */     this.FPPRun = false;
/* :39:740 */     this.FPPFlag = true;
/* :40:741 */     this.APTDump = false;
/* :41:742 */     setIntReq();
/* :42:    */   }
/* :43:    */   
/* :44:    */   class APT
/* :45:    */   {
/* :46:    */     private int[] reg;
/* :47:    */     
/* :48:    */     public APT()
/* :49:    */     {
/* :50:749 */       this.reg = new int[] { 0, 0, 0, 0, 0 };
/* :51:    */     }
/* :52:    */     
/* :53:    */     public int getInstr()
/* :54:    */     {
/* :55:756 */       int addr = getFPC();
/* :56:757 */       int instr = FPP.this.data.memory[addr] & 0xFFF;
/* :57:758 */       if (FPP.this.FPPdebug) {
/* :58:758 */         System.out.printf("%nFPC %05o", new Object[] { Integer.valueOf(addr) });
/* :59:    */       }
/* :60:759 */       setFPC(addr + 1);
/* :61:760 */       return instr;
/* :62:    */     }
/* :63:    */     
/* :64:    */     public int read(int addr)
/* :65:    */     {
/* :66:763 */       setOpadd(addr);
/* :67:764 */       return FPP.this.data.memory[(addr & 0x7FFF)] & 0xFFF;
/* :68:    */     }
/* :69:    */     
/* :70:    */     public void write(int addr, int word)
/* :71:    */     {
/* :72:767 */       setOpadd(addr);
/* :73:768 */       FPP.this.data.memory[(addr & 0x7FFF)] = (word & 0xFFF);
/* :74:    */     }
/* :75:    */     
/* :76:    */     public int getFPC()
/* :77:    */     {
/* :78:771 */       return this.reg[1] & 0xFFF | (this.reg[0] & 0x7) << 12;
/* :79:    */     }
/* :80:    */     
/* :81:    */     public void setFPC(int fpc)
/* :82:    */     {
/* :83:774 */       this.reg[0] = (fpc >> 12 & 0x7 | this.reg[0] & 0xFF8);
/* :84:775 */       this.reg[1] = (fpc & 0xFFF);
/* :85:    */     }
/* :86:    */     
/* :87:    */     public int readIndex(int xr)
/* :88:    */     {
/* :89:778 */       return FPP.this.data.memory[(getIndex() + xr & 0x7FFF)] & 0xFFF;
/* :90:    */     }
/* :91:    */     
/* :92:    */     public void writeIndex(int xr, int word)
/* :93:    */     {
/* :94:781 */       FPP.this.data.memory[(getIndex() + xr & 0x7FFF)] = (word & 0xFFF);
/* :95:    */     }
/* :96:    */     
/* :97:    */     public int readIndexOpa(int xr)
/* :98:    */     {
/* :99:784 */       return read(getIndex() + xr);
/* ;00:    */     }
/* ;01:    */     
/* ;02:    */     public void writeIndexOpa(int xr, int word)
/* ;03:    */     {
/* ;04:787 */       write(getIndex() + xr, word);
/* ;05:    */     }
/* ;06:    */     
/* ;07:    */     public int getIndex()
/* ;08:    */     {
/* ;09:790 */       return this.reg[2] & 0xFFF | (this.reg[0] & 0x38) << 9;
/* ;10:    */     }
/* ;11:    */     
/* ;12:    */     public void setIndex(int ind)
/* ;13:    */     {
/* ;14:793 */       this.reg[0] = (ind >> 9 & 0x38 | this.reg[0] & 0xFC7);
/* ;15:794 */       this.reg[2] = (ind & 0xFFF);
/* ;16:    */     }
/* ;17:    */     
/* ;18:    */     public int getBase()
/* ;19:    */     {
/* ;20:797 */       return this.reg[3] & 0xFFF | (this.reg[0] & 0x1C0) << 6;
/* ;21:    */     }
/* ;22:    */     
/* ;23:    */     public void setBase(int base)
/* ;24:    */     {
/* ;25:800 */       this.reg[0] = (base >> 6 & 0x1C0 | this.reg[0] & 0xE3F);
/* ;26:801 */       this.reg[3] = (base & 0xFFF);
/* ;27:    */     }
/* ;28:    */     
/* ;29:    */     public int getOpadd()
/* ;30:    */     {
/* ;31:804 */       return this.reg[4] & 0xFFF | (this.reg[0] & 0xE00) << 3;
/* ;32:    */     }
/* ;33:    */     
/* ;34:    */     public void setOpadd(int opadd)
/* ;35:    */     {
/* ;36:807 */       this.reg[0] = (opadd >> 3 & 0xE00 | this.reg[0] & 0x1FF);
/* ;37:808 */       this.reg[4] = (opadd & 0xFFF);
/* ;38:    */     }
/* ;39:    */     
/* ;40:    */     public void load(int addr)
/* ;41:    */     {
/* ;42:811 */       System.arraycopy(FPP.this.data.memory, addr, this.reg, 0, this.reg.length);
/* ;43:812 */       FPP.this.f.load(addr + this.reg.length, FPP.Mode.EP, false);
/* ;44:    */     }
/* ;45:    */     
/* ;46:    */     public void store(int addr)
/* ;47:    */     {
/* ;48:815 */       System.arraycopy(this.reg, 0, FPP.this.data.memory, addr, this.reg.length);
/* ;49:816 */       FPP.this.f.store(addr + this.reg.length, FPP.Mode.EP, false);
/* ;50:    */     }
/* ;51:    */   }
/* ;52:    */   
/* ;53:    */   class FAC
/* ;54:    */   {
/* ;55:    */     private int[] reg;
/* ;56:    */     public long m;
/* ;57:    */     public int e;
/* ;58:    */     public int s;
/* ;59:825 */     public long one = 4611686018427387904L;
/* ;60:    */     
/* ;61:    */     public FAC()
/* ;62:    */     {
/* ;63:828 */       this.reg = new int[] { 0, 0, 0, 0, 0, 0 };
/* ;64:    */     }
/* ;65:    */     
/* ;66:    */     public void load(int addr, FPP.Mode mode, boolean single)
/* ;67:    */     {
/* ;68:837 */       FPP.this.a.setOpadd(addr + 2 - mode.offs());
/* ;69:838 */       Arrays.fill(this.reg, 0);
/* ;70:839 */       FPP.Mode mod = (single & mode == FPP.Mode.DP) ? FPP.Mode.FP : mode;
/* ;71:840 */       System.arraycopy(FPP.this.data.memory, addr, this.reg, mod.offs(), mod.val());
/* ;72:841 */       if (mode != FPP.Mode.DP) {
/* ;73:841 */         this.e = (this.reg[0] << 20 >> 20);
/* ;74:    */       } else {
/* ;75:841 */         this.e = 0;
/* ;76:    */       }
/* ;77:842 */       this.m = 0L;
/* ;78:843 */       this.m |= (this.reg[1] & 0xFFF) << 52;
/* ;79:844 */       this.m |= (this.reg[2] & 0xFFF) << 40;
/* ;80:845 */       this.m |= (this.reg[3] & 0xFFF) << 28;
/* ;81:846 */       this.m |= (this.reg[4] & 0xFFF) << 16;
/* ;82:847 */       this.m |= (this.reg[5] & 0xFFF) << 4;
/* ;83:848 */       this.s = Long.signum(this.m);
/* ;84:    */     }
/* ;85:    */     
/* ;86:    */     public void store(int addr, FPP.Mode mode, boolean single)
/* ;87:    */     {
/* ;88:852 */       FPP.this.a.setOpadd(addr + 2);
/* ;89:853 */       save(mode);
/* ;90:854 */       FPP.Mode mod = (single & mode == FPP.Mode.DP) ? FPP.Mode.FP : mode;
/* ;91:855 */       System.arraycopy(this.reg, mod.offs(), FPP.this.data.memory, addr, mod.val());
/* ;92:    */     }
/* ;93:    */     
/* ;94:    */     public void copyTo(FAC x)
/* ;95:    */     {
/* ;96:859 */       save(FPP.this.mode);
/* ;97:860 */       System.arraycopy(this.reg, FPP.this.mode.offs(), x.reg, FPP.this.mode.offs(), FPP.this.mode.val());
/* ;98:861 */       x.e = (x.reg[0] << 20 >> 20);
/* ;99:862 */       x.m = 0L;
/* <00:863 */       x.m |= (x.reg[1] & 0xFFF) << 52;
/* <01:864 */       x.m |= (x.reg[2] & 0xFFF) << 40;
/* <02:865 */       x.m |= (x.reg[3] & 0xFFF) << 28;
/* <03:866 */       x.m |= (x.reg[4] & 0xFFF) << 16;
/* <04:867 */       x.m |= (x.reg[5] & 0xFFF) << 4;
/* <05:868 */       x.s = Long.signum(x.m);
/* <06:    */     }
/* <07:    */     
/* <08:    */     public void clear()
/* <09:    */     {
/* <10:872 */       this.e = 0;
/* <11:873 */       this.m = 0L;
/* <12:874 */       this.s = 0;
/* <13:    */     }
/* <14:    */     
/* <15:    */     public void save(FPP.Mode mod)
/* <16:    */     {
/* <17:878 */       if (mod != FPP.Mode.DP) {
/* <18:878 */         this.reg[0] = (this.e & 0xFFF);
/* <19:    */       } else {
/* <20:878 */         this.reg[0] = 0;
/* <21:    */       }
/* <22:879 */       this.reg[1] = ((int)(this.m >>> 52 & 0xFFF));
/* <23:880 */       this.reg[2] = ((int)(this.m >>> 40 & 0xFFF));
/* <24:881 */       this.reg[3] = ((int)(this.m >>> 28 & 0xFFF));
/* <25:882 */       this.reg[4] = ((int)(this.m >>> 16 & 0xFFF));
/* <26:883 */       this.reg[5] = ((int)(this.m >>> 4 & 0xFFF));
/* <27:    */     }
/* <28:    */     
/* <29:    */     public boolean mnorm()
/* <30:    */     {
/* <31:887 */       if (this.m == 0L)
/* <32:    */       {
/* <33:888 */         this.e = 0;
/* <34:889 */         return false;
/* <35:    */       }
/* <36:891 */       while (((this.m ^ this.m << 1) & FPP.hibit) == 0L)
/* <37:    */       {
/* <38:892 */         this.m <<= 1;
/* <39:893 */         this.e -= 1;
/* <40:    */       }
/* <41:895 */       if (this.m << 1 == 0L)
/* <42:    */       {
/* <43:896 */         this.m >>= 1;
/* <44:897 */         this.e += 1;
/* <45:898 */         return false;
/* <46:    */       }
/* <47:900 */       return true;
/* <48:    */     }
/* <49:    */     
/* <50:    */     public void mround()
/* <51:    */     {
/* <52:904 */       switch (FPP.1.$SwitchMap$Logic$FPP$Mode[FPP.this.mode.ordinal()])
/* <53:    */       {
/* <54:    */       case 1: 
/* <55:906 */         this.m &= FPP.Mode.access$1700(FPP.Mode.DP);
/* <56:907 */         break;
/* <57:    */       case 2: 
/* <58:909 */         this.m &= FPP.Mode.access$1700(FPP.Mode.EP);
/* <59:910 */         break;
/* <60:    */       case 3: 
/* <61:912 */         this.m &= FPP.Mode.access$1700(FPP.Mode.FP);
/* <62:    */       }
/* <63:    */     }
/* <64:    */     
/* <65:    */     public void mshift(int diff)
/* <66:    */     {
/* <67:919 */       int shift = Math.min(63, Math.abs(diff));
/* <68:920 */       if (diff > 0) {
/* <69:920 */         this.m >>= shift;
/* <70:    */       } else {
/* <71:921 */         this.m <<= shift;
/* <72:    */       }
/* <73:922 */       this.m &= FPP.Mode.access$1700(FPP.this.mode);
/* <74:923 */       if (this.m == 0L) {
/* <75:923 */         this.e = 0;
/* <76:    */       }
/* <77:    */     }
/* <78:    */     
/* <79:    */     public void madd(FAC x)
/* <80:    */     {
/* <81:927 */       if (x.e > this.e)
/* <82:    */       {
/* <83:928 */         mshift(x.e - this.e);
/* <84:929 */         this.e = x.e;
/* <85:    */       }
/* <86:    */       else
/* <87:    */       {
/* <88:931 */         x.mshift(this.e - x.e);
/* <89:    */       }
/* <90:933 */       this.m = ((x.m >> 1) + (this.m >> 1));
/* <91:934 */       this.e += 1;
/* <92:    */     }
/* <93:    */     
/* <94:    */     public void mmul(FAC x)
/* <95:    */     {
/* <96:939 */       long m_tm = 0L;
/* <97:940 */       long m_ta = this.m;
/* <98:    */       
/* <99:942 */       int ie = (FPP.Mode.access$2000(FPP.this.mode) + FPP.Mode.access$2100(FPP.this.mode) - 1) * 12 - 1 + 4;
/* =00:943 */       for (int i = 0; i < ie; i++)
/* =01:    */       {
/* =02:944 */         if ((m_ta & FPP.Mode.access$1800(FPP.this.mode) >> 4) != 0L) {
/* =03:945 */           m_tm += x.m;
/* =04:    */         }
/* =05:947 */         m_tm >>>= 1;
/* =06:948 */         m_ta >>>= 1;
/* =07:    */       }
/* =08:950 */       this.e += x.e;
/* =09:951 */       this.m = (this.s * x.s < 0 ? -m_tm : m_tm);
/* =10:952 */       this.s = Long.signum(this.m);
/* =11:    */     }
/* =12:    */     
/* =13:    */     public void mdiv(FAC x)
/* =14:    */     {
/* =15:956 */       int i = 0;
/* =16:957 */       long m_tm = 0L;
/* =17:958 */       long m_ta = this.m;
/* =18:959 */       long m_op = x.m;
/* =19:960 */       m_ta -= m_op;
/* =20:961 */       int ie = (FPP.Mode.access$2000(FPP.this.mode) + FPP.Mode.access$2100(FPP.this.mode) - 1) * 12 - 1 + 4;
/* =21:    */       do
/* =22:    */       {
/* =23:963 */         if (m_ta < 0L)
/* =24:    */         {
/* =25:964 */           m_ta = m_ta << 1 | (m_tm < 0L ? 1L : 0L);
/* =26:965 */           m_tm <<= 1;
/* =27:966 */           m_tm += 0L;
/* =28:967 */           m_ta += m_op;
/* =29:    */         }
/* =30:    */         else
/* =31:    */         {
/* =32:969 */           m_ta = m_ta << 1 | (m_tm < 0L ? 1L : 0L);
/* =33:970 */           m_tm <<= 1;
/* =34:971 */           m_tm += (FPP.Mode.access$1800(FPP.this.mode) >> 4);
/* =35:972 */           m_ta -= m_op;
/* =36:    */         }
/* =37:974 */         i++;
/* =38:975 */       } while (i < ie);
/* =39:976 */       if (m_ta < 0L) {
/* =40:976 */         m_ta += m_op;
/* =41:    */       }
/* =42:977 */       this.m = (x.s * this.s < 0 ? -m_tm : m_tm);
/* =43:978 */       this.s = Long.signum(this.m);
/* =44:979 */       this.e = (this.e - x.e + 1);
/* =45:    */     }
/* =46:    */   }
/* =47:    */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Logic.FPP
 * JD-Core Version:    0.7.0.1
 */