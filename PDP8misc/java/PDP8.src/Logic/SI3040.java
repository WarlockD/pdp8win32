/*   1:    */ package Logic;
/*   2:    */ 
/*   3:    */ import Devices.Disk4043;
/*   4:    */ import Devices.DiskUnit;
/*   5:    */ import java.awt.Color;
/*   6:    */ import java.io.IOException;
/*   7:    */ import java.io.PrintStream;
/*   8:    */ import java.io.RandomAccessFile;
/*   9:    */ 
/*  10:    */ public class SI3040
/*  11:    */   implements Device, Constants
/*  12:    */ {
/*  13: 21 */   public static int DevId50 = 40;
/*  14: 22 */   public static int DevId51 = 41;
/*  15:    */   public Disk4043 disk4043;
/*  16:    */   public BusRegMem data;
/*  17:    */   private int ctrlreg;
/*  18: 57 */   private int ema = 0;
/*  19: 58 */   private boolean intena = false;
/*  20: 59 */   private boolean frmena = false;
/*  21: 60 */   private int unit = 0;
/*  22: 61 */   private boolean rw = false;
/*  23: 62 */   private boolean busyflag = false;
/*  24: 63 */   private boolean doneflag = false;
/*  25: 64 */   public int siunit = 0;
/*  26:    */   private int statreg;
/*  27:104 */   private boolean errorflag = false;
/*  28:105 */   private boolean selerr = false;
/*  29:106 */   private boolean frmerr = false;
/*  30:107 */   private boolean adrerr = false;
/*  31:108 */   private boolean logerr = false;
/*  32:109 */   private boolean wloerr = false;
/*  33:    */   private int seekstatreg;
/*  34:147 */   private boolean[] seekerr = { false, false, false, false };
/*  35:148 */   private boolean[] busyerr = { false, false, false, false };
/*  36:149 */   private boolean[] busy = { false, false, false, false };
/*  37:    */   private int seekreg;
/*  38:    */   private int seekunit;
/*  39:    */   private int trackreg;
/*  40:189 */   private int removable = 0;
/*  41:    */   private int sectreg;
/*  42:    */   private int wcreg;
/*  43:    */   private int addrreg;
/*  44:    */   private int diskaddr;
/*  45:    */   private int sectcntreg;
/*  46:    */   private boolean pseudobusy;
/*  47:215 */   public boolean fmton = false;
/*  48:216 */   private boolean wlock = false;
/*  49:218 */   public boolean[][] sel = { { false, false }, { false, false }, { false, false }, { false, false } };
/*  50:219 */   public RandomAccessFile[][] disk = { { null, null }, { null, null }, { null, null }, { null, null } };
/*  51:220 */   public int[] track = { 0, 0, 0, 0 };
/*  52:    */   private VirTimer puntim;
/*  53:    */   private VirTimer rdrtim;
/*  54:    */   private VirTimer stoptim;
/*  55:    */   
/*  56:    */   public SI3040(BusRegMem data)
/*  57:    */   {
/*  58:228 */     this.data = data;
/*  59:229 */     this.disk4043 = new Disk4043(this);
/*  60:230 */     this.disk4043.setVisible(true);
/*  61:    */     
/*  62:232 */     VirListener punaction = new VirListener()
/*  63:    */     {
/*  64:    */       public void actionPerformed() {}
/*  65:236 */     };
/*  66:237 */     this.puntim = new VirTimer(data.virqueue, 200000, punaction);
/*  67:238 */     this.puntim.setRepeats(false);
/*  68:    */     
/*  69:240 */     VirListener rdraction = new VirListener()
/*  70:    */     {
/*  71:    */       public void actionPerformed() {}
/*  72:244 */     };
/*  73:245 */     this.rdrtim = new VirTimer(data.virqueue, 33333, rdraction);
/*  74:246 */     this.rdrtim.setRepeats(false);
/*  75:    */     
/*  76:248 */     VirListener stopaction = new VirListener()
/*  77:    */     {
/*  78:    */       public void actionPerformed() {}
/*  79:252 */     };
/*  80:253 */     this.stoptim = new VirTimer(data.virqueue, 400000, stopaction);
/*  81:254 */     this.stoptim.setRepeats(false);
/*  82:    */   }
/*  83:    */   
/*  84:    */   public void Decode(int devcode, int opcode)
/*  85:    */   {
/*  86:258 */     if (devcode == DevId50) {
/*  87:259 */       switch (opcode)
/*  88:    */       {
/*  89:    */       case 1: 
/*  90:    */         break;
/*  91:    */       case 2: 
/*  92:261 */         this.data.c0c1c2 = 0; break;
/*  93:    */       case 3: 
/*  94:262 */         this.data.c0c1c2 = 3; break;
/*  95:    */       case 4: 
/*  96:    */         break;
/*  97:    */       case 5: 
/*  98:264 */         this.data.c0c1c2 = 3; break;
/*  99:    */       case 6: 
/* 100:265 */         this.data.c0c1c2 = 0; break;
/* 101:    */       case 7: 
/* 102:266 */         this.data.c0c1c2 = 3;
/* 103:    */       }
/* 104:    */     }
/* 105:269 */     if (devcode == DevId51) {
/* 106:270 */       switch (opcode)
/* 107:    */       {
/* 108:    */       case 1: 
/* 109:    */         break;
/* 110:    */       case 2: 
/* 111:272 */         this.data.c0c1c2 = 0; break;
/* 112:    */       case 3: 
/* 113:273 */         this.data.c0c1c2 = 3; break;
/* 114:    */       case 4: 
/* 115:274 */         this.data.c0c1c2 = 0; break;
/* 116:    */       case 5: 
/* 117:275 */         this.data.c0c1c2 = 0; break;
/* 118:    */       case 6: 
/* 119:276 */         this.data.c0c1c2 = 3; break;
/* 120:    */       case 7: 
/* 121:277 */         this.data.c0c1c2 = 0;
/* 122:    */       }
/* 123:    */     }
/* 124:    */   }
/* 125:    */   
/* 126:    */   public void Execute(int devcode, int opcode)
/* 127:    */   {
/* 128:283 */     int temp = this.data.data;
/* 129:284 */     if (devcode == DevId50) {
/* 130:285 */       switch (opcode)
/* 131:    */       {
/* 132:    */       case 1: 
/* 133:286 */         this.data.skipbus = this.doneflag; break;
/* 134:    */       case 2: 
/* 135:287 */         this.ctrlreg = this.data.data;setCtrl(); break;
/* 136:    */       case 3: 
/* 137:288 */         getCtrl();this.data.data = this.ctrlreg; break;
/* 138:    */       case 4: 
/* 139:289 */         this.statreg = 0;setStat(); break;
/* 140:    */       case 5: 
/* 141:290 */         getStat();this.data.data = this.statreg; break;
/* 142:    */       case 6: 
/* 143:291 */         this.seekreg = this.data.data;goSeek(); break;
/* 144:    */       case 7: 
/* 145:292 */         getSeekStat();this.data.data = this.seekstatreg;
/* 146:    */       }
/* 147:    */     }
/* 148:295 */     if (devcode == DevId51) {
/* 149:296 */       switch (opcode)
/* 150:    */       {
/* 151:    */       case 1: 
/* 152:297 */         this.data.skipbus = this.errorflag; break;
/* 153:    */       case 2: 
/* 154:298 */         this.sectreg = (this.data.data & 0xF); break;
/* 155:    */       case 3: 
/* 156:299 */         this.data.data = this.sectreg; break;
/* 157:    */       case 4: 
/* 158:300 */         this.trackreg = this.data.data;goRead(); break;
/* 159:    */       case 5: 
/* 160:301 */         this.trackreg = this.data.data;goWrite(); break;
/* 161:    */       case 6: 
/* 162:302 */         this.data.data = this.trackreg; break;
/* 163:    */       case 7: 
/* 164:303 */         this.seekreg = this.data.data;setWcAdr();
/* 165:    */       }
/* 166:    */     }
/* 167:    */   }
/* 168:    */   
/* 169:    */   public void clearIntReq()
/* 170:    */   {
/* 171:310 */     if (!this.doneflag) {
/* 172:311 */       this.data.setIntReq(DevId50, false);
/* 173:    */     }
/* 174:    */   }
/* 175:    */   
/* 176:    */   public void setIntReq()
/* 177:    */   {
/* 178:316 */     if (this.doneflag == true) {
/* 179:316 */       this.data.setIntReq(DevId50, this.intena);
/* 180:    */     } else {
/* 181:317 */       this.data.setIntReq(DevId50, false);
/* 182:    */     }
/* 183:    */   }
/* 184:    */   
/* 185:    */   private void setCtrl()
/* 186:    */   {
/* 187:321 */     this.ema = ((this.ctrlreg & 0xE00) << 3);
/* 188:322 */     if ((this.ctrlreg & 0x40) > 0) {
/* 189:322 */       this.intena = true;
/* 190:    */     } else {
/* 191:322 */       this.intena = false;
/* 192:    */     }
/* 193:323 */     if ((this.ctrlreg & 0x20) > 0) {
/* 194:324 */       this.frmena = true;
/* 195:    */     } else {
/* 196:325 */       this.frmena = false;
/* 197:    */     }
/* 198:326 */     this.unit = (((this.ctrlreg & 0x18) >> 3) + this.siunit);
/* 199:327 */     if ((this.ctrlreg & 0x4) > 0) {
/* 200:327 */       this.rw = true;
/* 201:    */     } else {
/* 202:327 */       this.rw = false;
/* 203:    */     }
/* 204:328 */     if ((this.ctrlreg & 0x2) > 0) {
/* 205:328 */       this.busyflag = true;
/* 206:    */     } else {
/* 207:328 */       this.busyflag = false;
/* 208:    */     }
/* 209:329 */     if ((this.ctrlreg & 0x1) > 0) {
/* 210:329 */       this.doneflag = true;
/* 211:    */     } else {
/* 212:329 */       this.doneflag = false;
/* 213:    */     }
/* 214:330 */     setIntReq();
/* 215:331 */     if (this.siunit == 1) {
/* 216:331 */       this.disk4043.diskUnit0.setBackground(new Color(0, 0, 0));
/* 217:    */     } else {
/* 218:332 */       this.disk4043.diskUnit0.setBackground(new Color(100, 100, 100));
/* 219:    */     }
/* 220:    */   }
/* 221:    */   
/* 222:    */   private void getCtrl()
/* 223:    */   {
/* 224:336 */     this.ctrlreg = (this.ema >> 3);
/* 225:337 */     if (this.intena) {
/* 226:337 */       this.ctrlreg |= 0x40;
/* 227:    */     }
/* 228:338 */     if (this.frmena) {
/* 229:338 */       this.ctrlreg |= 0x20;
/* 230:    */     }
/* 231:339 */     this.ctrlreg |= (this.unit > 0 ? this.unit - this.siunit : this.unit) << 3 & 0x18;
/* 232:340 */     if (this.rw) {
/* 233:340 */       this.ctrlreg |= 0x4;
/* 234:    */     }
/* 235:341 */     if (this.busyflag) {
/* 236:341 */       this.ctrlreg |= 0x2;
/* 237:    */     }
/* 238:342 */     if (this.doneflag) {
/* 239:342 */       this.ctrlreg |= 0x1;
/* 240:    */     }
/* 241:    */   }
/* 242:    */   
/* 243:    */   private void setStat()
/* 244:    */   {
/* 245:346 */     if ((this.statreg & 0x800) > 0) {
/* 246:346 */       this.errorflag = true;
/* 247:    */     } else {
/* 248:346 */       this.errorflag = false;
/* 249:    */     }
/* 250:347 */     if ((this.statreg & 0x400) > 0) {
/* 251:347 */       this.selerr = true;
/* 252:    */     } else {
/* 253:347 */       this.selerr = false;
/* 254:    */     }
/* 255:348 */     if ((this.statreg & 0x200) > 0) {
/* 256:348 */       this.frmerr = true;
/* 257:    */     } else {
/* 258:348 */       this.frmerr = false;
/* 259:    */     }
/* 260:349 */     if ((this.statreg & 0x100) > 0) {
/* 261:349 */       this.adrerr = true;
/* 262:    */     } else {
/* 263:349 */       this.adrerr = false;
/* 264:    */     }
/* 265:350 */     if ((this.statreg & 0x80) > 0) {
/* 266:350 */       this.logerr = true;
/* 267:    */     } else {
/* 268:350 */       this.logerr = false;
/* 269:    */     }
/* 270:351 */     if ((this.statreg & 0x20) > 0) {
/* 271:351 */       this.wloerr = true;
/* 272:    */     } else {
/* 273:351 */       this.wloerr = false;
/* 274:    */     }
/* 275:352 */     if ((this.statreg & 0x2) > 0) {
/* 276:352 */       this.busyflag = true;
/* 277:    */     } else {
/* 278:352 */       this.busyflag = false;
/* 279:    */     }
/* 280:353 */     if ((this.statreg & 0x1) > 0) {
/* 281:353 */       this.doneflag = true;
/* 282:    */     } else {
/* 283:353 */       this.doneflag = false;
/* 284:    */     }
/* 285:354 */     setIntReq();
/* 286:    */   }
/* 287:    */   
/* 288:    */   private void getStat()
/* 289:    */   {
/* 290:358 */     this.statreg = 0;
/* 291:359 */     if (this.errorflag) {
/* 292:359 */       this.statreg |= 0x800;
/* 293:    */     }
/* 294:360 */     if (this.selerr) {
/* 295:360 */       this.statreg |= 0x400;
/* 296:    */     }
/* 297:361 */     if (this.frmerr) {
/* 298:361 */       this.statreg |= 0x200;
/* 299:    */     }
/* 300:362 */     if (this.adrerr) {
/* 301:362 */       this.statreg |= 0x100;
/* 302:    */     }
/* 303:363 */     if (this.logerr) {
/* 304:363 */       this.statreg |= 0x80;
/* 305:    */     }
/* 306:364 */     if (this.wloerr) {
/* 307:364 */       this.statreg |= 0x20;
/* 308:    */     }
/* 309:365 */     if (this.busyflag) {
/* 310:365 */       this.statreg |= 0x2;
/* 311:    */     }
/* 312:366 */     if (this.doneflag) {
/* 313:366 */       this.statreg |= 0x1;
/* 314:    */     }
/* 315:    */   }
/* 316:    */   
/* 317:    */   private void getSeekStat()
/* 318:    */   {
/* 319:370 */     this.seekstatreg = 0;
/* 320:371 */     for (int i = 0; i < 4; i++)
/* 321:    */     {
/* 322:372 */       if (this.seekerr[i] != 0) {
/* 323:372 */         this.seekstatreg |= 4 << i * 3;
/* 324:    */       }
/* 325:373 */       if (this.busyerr[i] != 0) {
/* 326:373 */         this.seekstatreg |= 2 << i * 3;
/* 327:    */       }
/* 328:374 */       if (this.busy[i] != 0) {
/* 329:374 */         this.seekstatreg |= 1 << i * 3;
/* 330:    */       }
/* 331:    */     }
/* 332:    */   }
/* 333:    */   
/* 334:    */   private void goSeek()
/* 335:    */   {
/* 336:379 */     this.seekunit = ((this.seekreg & 0xC00) >> 10);
/* 337:380 */     this.seekreg &= 0x3FF;
/* 338:381 */     if ((this.sel[this.seekunit][1] | this.sel[this.seekunit][0]) != 0)
/* 339:    */     {
/* 340:382 */       this.seekerr[this.seekunit] = false;
/* 341:383 */       this.busyerr[this.seekunit] = false;
/* 342:    */     }
/* 343:385 */     setBlock(false, this.seekunit, this.seekreg);
/* 344:    */   }
/* 345:    */   
/* 346:    */   private void goRead()
/* 347:    */   {
/* 348:389 */     this.pseudobusy = false;
/* 349:390 */     this.removable = ((this.trackreg & 0x400) >> 10);
/* 350:    */     do
/* 351:    */     {
/* 352:392 */       setBlock(true, this.unit, this.trackreg & 0x3FF);
/* 353:393 */     } while ((readSect() >= 0) && 
/* 354:394 */       (this.wcreg > 0));
/* 355:    */   }
/* 356:    */   
/* 357:    */   private void goWrite()
/* 358:    */   {
/* 359:398 */     this.pseudobusy = false;
/* 360:399 */     this.removable = ((this.trackreg & 0x400) >> 10);
/* 361:    */     do
/* 362:    */     {
/* 363:401 */       setBlock(true, this.unit, this.trackreg & 0x3FF);
/* 364:402 */     } while ((writeSect() >= 0) && 
/* 365:403 */       (this.wcreg > 0));
/* 366:    */   }
/* 367:    */   
/* 368:    */   private void setWcAdr()
/* 369:    */   {
/* 370:407 */     this.trackreg = this.seekreg;
/* 371:408 */     this.addrreg = this.trackreg;
/* 372:409 */     this.wcreg = this.data.memory[(this.ema | this.addrreg)];
/* 373:410 */     this.addrreg = this.data.memory[(this.ema | this.addrreg + 1)];
/* 374:411 */     this.pseudobusy = true;
/* 375:    */   }
/* 376:    */   
/* 377:    */   private void setBlock(boolean rdwr, int aunit, int block)
/* 378:    */   {
/* 379:416 */     if ((this.sel[aunit][1] | this.sel[aunit][0]) != 0)
/* 380:    */     {
/* 381:417 */       this.doneflag = true;
/* 382:    */     }
/* 383:    */     else
/* 384:    */     {
/* 385:419 */       this.selerr = true;
/* 386:420 */       this.errorflag = true;
/* 387:421 */       return;
/* 388:    */     }
/* 389:423 */     if (block == 0)
/* 390:    */     {
/* 391:424 */       this.seekerr[aunit] = false;
/* 392:425 */       this.busyerr[aunit] = false;
/* 393:    */     }
/* 394:427 */     if (block > 407)
/* 395:    */     {
/* 396:428 */       this.logerr = true;
/* 397:429 */       this.errorflag = true;
/* 398:430 */       this.seekerr[aunit] = true;
/* 399:431 */       this.busyflag = false;
/* 400:432 */       return;
/* 401:    */     }
/* 402:434 */     if (this.pseudobusy)
/* 403:    */     {
/* 404:435 */       this.doneflag = false;
/* 405:436 */       if (((aunit == this.unit ? 1 : 0) | this.busy[aunit]) != 0)
/* 406:    */       {
/* 407:437 */         this.busyerr[aunit] = true;
/* 408:438 */         this.busyflag = true;
/* 409:    */       }
/* 410:    */     }
/* 411:441 */     this.diskaddr = (block << 4 | this.sectreg);
/* 412:442 */     this.track[aunit] = block;
/* 413:443 */     if ((rdwr & !this.frmena & this.sel[aunit][this.removable])) {
/* 414:    */       try
/* 415:    */       {
/* 416:445 */         this.disk[aunit][this.removable].seek(30 + this.diskaddr * 417);
/* 417:446 */         int taw = this.disk[this.unit][this.removable].readUnsignedShort() >> 4;
/* 418:448 */         if (block != (taw & 0x3FF))
/* 419:    */         {
/* 420:449 */           this.adrerr = true;
/* 421:450 */           this.errorflag = true;
/* 422:    */         }
/* 423:452 */         if ((taw & 0x800) > 0) {
/* 424:452 */           this.wlock = true;
/* 425:    */         } else {
/* 426:452 */           this.wlock = false;
/* 427:    */         }
/* 428:    */       }
/* 429:    */       catch (IOException e)
/* 430:    */       {
/* 431:455 */         System.out.println(e);
/* 432:    */       }
/* 433:    */     }
/* 434:    */   }
/* 435:    */   
/* 436:    */   private int readSect()
/* 437:    */   {
/* 438:462 */     int cnt = 256;
/* 439:463 */     int offs = 33;
/* 440:464 */     if (this.sel[this.unit][this.removable] == 0)
/* 441:    */     {
/* 442:465 */       this.selerr = true;
/* 443:466 */       this.errorflag = true;
/* 444:467 */       return -1;
/* 445:    */     }
/* 446:469 */     if (this.wcreg == 0) {
/* 447:469 */       cnt = 0;
/* 448:    */     }
/* 449:    */     try
/* 450:    */     {
/* 451:471 */       this.disk[this.unit][this.removable].seek(offs + this.diskaddr * 417);
/* 452:472 */       while (((this.wcreg > 0 ? 1 : 0) & (cnt > 0 ? 1 : 0)) != 0)
/* 453:    */       {
/* 454:473 */         int byte1 = this.disk[this.unit][this.removable].read();
/* 455:474 */         int byte2 = this.disk[this.unit][this.removable].read();
/* 456:475 */         int byte3 = this.disk[this.unit][this.removable].read();
/* 457:476 */         if (((byte1 < 0 ? 1 : 0) | (byte2 < 0 ? 1 : 0) | (byte3 < 0 ? 1 : 0)) != 0) {
/* 458:476 */           return -1;
/* 459:    */         }
/* 460:477 */         this.data.memory[(this.ema | this.addrreg)] = (byte1 << 4);
/* 461:478 */         this.data.memory[(this.ema | this.addrreg++)] |= byte2 >> 4;
/* 462:479 */         this.wcreg -= 1;cnt--;
/* 463:480 */         if (this.wcreg > 0)
/* 464:    */         {
/* 465:481 */           this.data.memory[(this.ema | this.addrreg)] = ((byte2 & 0xF) << 8);
/* 466:482 */           this.data.memory[(this.ema | this.addrreg++)] |= byte3;
/* 467:483 */           this.wcreg -= 1;cnt--;
/* 468:    */         }
/* 469:    */       }
/* 470:    */     }
/* 471:    */     catch (IOException e)
/* 472:    */     {
/* 473:487 */       System.out.println(e);
/* 474:    */     }
/* 475:489 */     this.diskaddr += 1;
/* 476:490 */     this.sectreg = (this.diskaddr & 0xF);
/* 477:491 */     this.trackreg = (this.diskaddr >> 4 | this.removable << 10);
/* 478:492 */     this.track[this.unit] = (this.trackreg & 0x3FF);
/* 479:493 */     return 0;
/* 480:    */   }
/* 481:    */   
/* 482:    */   private int writeSect()
/* 483:    */   {
/* 484:497 */     int byte3 = 0;
/* 485:498 */     int cnt = 256;
/* 486:499 */     int offs = 33;
/* 487:500 */     if (this.sel[this.unit][this.removable] == 0)
/* 488:    */     {
/* 489:501 */       this.selerr = true;
/* 490:502 */       this.errorflag = true;
/* 491:503 */       return -1;
/* 492:    */     }
/* 493:505 */     if (this.frmena) {
/* 494:506 */       if (this.fmton)
/* 495:    */       {
/* 496:507 */         offs = 0;
/* 497:    */       }
/* 498:    */       else
/* 499:    */       {
/* 500:509 */         this.frmerr = true;
/* 501:510 */         this.errorflag = true;
/* 502:511 */         return -1;
/* 503:    */       }
/* 504:    */     }
/* 505:514 */     if ((this.wlock & !this.fmton))
/* 506:    */     {
/* 507:515 */       this.wloerr = true;
/* 508:516 */       this.errorflag = true;
/* 509:517 */       return -1;
/* 510:    */     }
/* 511:519 */     if (this.wcreg == 0) {
/* 512:519 */       cnt = 0;
/* 513:    */     }
/* 514:    */     try
/* 515:    */     {
/* 516:521 */       this.disk[this.unit][this.removable].seek(offs + this.diskaddr * 417);
/* 517:522 */       while (((this.wcreg > 0 ? 1 : 0) & (cnt > 0 ? 1 : 0)) != 0)
/* 518:    */       {
/* 519:523 */         int byte1 = this.data.memory[(this.ema | this.addrreg)] >> 4;
/* 520:524 */         this.disk[this.unit][this.removable].write(byte1);
/* 521:525 */         int byte2 = (this.data.memory[(this.ema | this.addrreg++)] & 0xF) << 4;
/* 522:526 */         this.wcreg -= 1;cnt--;
/* 523:527 */         if (this.wcreg > 0)
/* 524:    */         {
/* 525:528 */           byte2 |= this.data.memory[(this.ema | this.addrreg)] >> 8;
/* 526:529 */           this.disk[this.unit][this.removable].write(byte2);
/* 527:530 */           byte3 = this.data.memory[(this.ema | this.addrreg++)] & 0xFF;
/* 528:531 */           this.disk[this.unit][this.removable].write(byte3);
/* 529:532 */           this.wcreg -= 1;cnt--;
/* 530:    */         }
/* 531:    */         else
/* 532:    */         {
/* 533:534 */           this.disk[this.unit][this.removable].write(byte2);
/* 534:    */         }
/* 535:    */       }
/* 536:536 */       while (cnt > 0)
/* 537:    */       {
/* 538:537 */         cnt--;
/* 539:538 */         cnt--;
/* 540:539 */         this.disk[this.unit][this.removable].write(0);
/* 541:540 */         this.disk[this.unit][this.removable].write(0);
/* 542:541 */         this.disk[this.unit][this.removable].write(0);
/* 543:    */       }
/* 544:    */     }
/* 545:    */     catch (IOException e)
/* 546:    */     {
/* 547:544 */       System.out.println(e);
/* 548:    */     }
/* 549:546 */     if (!this.frmena)
/* 550:    */     {
/* 551:547 */       this.diskaddr += 1;
/* 552:548 */       this.sectreg = (this.diskaddr & 0xF);
/* 553:549 */       this.trackreg = (this.diskaddr >> 4 | this.removable << 10);
/* 554:    */     }
/* 555:551 */     this.track[this.unit] = (this.trackreg & 0x3FF);
/* 556:552 */     return 0;
/* 557:    */   }
/* 558:    */   
/* 559:    */   public void ClearFlags(int devcode)
/* 560:    */   {
/* 561:557 */     this.ctrlreg = 0;
/* 562:558 */     this.ema = 0;
/* 563:559 */     this.intena = false;
/* 564:560 */     this.frmena = false;
/* 565:561 */     this.unit = this.siunit;
/* 566:562 */     this.rw = false;
/* 567:563 */     this.doneflag = false;
/* 568:564 */     this.busyflag = false;
/* 569:    */     
/* 570:566 */     this.statreg = 0;
/* 571:567 */     this.errorflag = false;
/* 572:568 */     this.selerr = false;
/* 573:569 */     this.frmerr = false;
/* 574:570 */     this.adrerr = false;
/* 575:571 */     this.logerr = false;
/* 576:572 */     this.wloerr = false;
/* 577:    */     
/* 578:574 */     this.seekstatreg = 0;
/* 579:575 */     this.seekreg = 0;
/* 580:576 */     this.sectreg = 0;
/* 581:577 */     this.trackreg = 0;
/* 582:578 */     this.addrreg = 0;
/* 583:579 */     this.data.setIntReq(DevId50, this.intena);
/* 584:    */   }
/* 585:    */   
/* 586:    */   public void Interrupt(int command)
/* 587:    */   {
/* 588:584 */     this.siunit = command;
/* 589:    */   }
/* 590:    */   
/* 591:    */   public void ClearRun(boolean run)
/* 592:    */   {
/* 593:588 */     if (!run) {}
/* 594:    */   }
/* 595:    */   
/* 596:    */   public void CloseDev(int devcode)
/* 597:    */   {
/* 598:593 */     for (int u = 0; u < 2; u++) {
/* 599:594 */       for (int r = 0; r < 2; r++) {
/* 600:595 */         if (this.sel[u][r] != 0) {
/* 601:596 */           this.disk4043.closeDisk(u, r, true);
/* 602:    */         }
/* 603:    */       }
/* 604:    */     }
/* 605:    */   }
/* 606:    */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Logic.SI3040
 * JD-Core Version:    0.7.0.1
 */