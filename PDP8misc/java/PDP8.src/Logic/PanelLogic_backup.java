/*   1:    */ package Logic;
/*   2:    */ 
/*   3:    */ import Panel.AcLamps;
/*   4:    */ import Panel.BusLamps;
/*   5:    */ import Panel.DataLamps;
/*   6:    */ import Panel.EmaLamps;
/*   7:    */ import Panel.Lamp;
/*   8:    */ import Panel.MaLamps;
/*   9:    */ import Panel.MdLamps;
/*  10:    */ import Panel.MqLamps;
/*  11:    */ import Panel.Panel8;
/*  12:    */ import Panel.StateLamps;
/*  13:    */ import Panel.StatusLamps;
/*  14:    */ 
/*  15:    */ public class PanelLogic_backup
/*  16:    */   extends Thread
/*  17:    */   implements Constants
/*  18:    */ {
/*  19:    */   public BusRegMem data;
/*  20:    */   private Panel8 panel;
/*  21:    */   
/*  22:    */   public PanelLogic_backup(BusRegMem data, String name)
/*  23:    */   {
/*  24: 16 */     super(name);
/*  25: 17 */     this.data = data;
/*  26: 18 */     this.power = 1;
/*  27:    */   }
/*  28:    */   
/*  29: 22 */   protected int select = 0;
/*  30:    */   protected int power;
/*  31:    */   protected boolean eal;
/*  32:    */   protected boolean al;
/*  33:    */   protected boolean clear;
/*  34:    */   protected boolean cont;
/*  35:    */   protected boolean exam;
/*  36:    */   protected boolean dep;
/*  37:    */   
/*  38:    */   public static enum BootType
/*  39:    */   {
/*  40: 33 */     SI3040_0,  SI3040_1,  TD8E,  BINLOADHI,  BINLOADLO,  RIMLOADHI,  RIMLOADLO,  BINPUNCHHI;
/*  41:    */     
/*  42:    */     private BootType() {}
/*  43:    */   }
/*  44:    */   
/*  45:    */   public void menuContext(int menu)
/*  46:    */   {
/*  47: 37 */     if (this.data.devices[PowerBoot.DevId10] != null) {
/*  48: 38 */       switch (menu)
/*  49:    */       {
/*  50:    */       case 0: 
/*  51: 39 */         this.data.devices[SI3040.DevId50].Interrupt(0);
/*  52: 40 */         this.data.devices[PowerBoot.DevId10].Interrupt(196608); break;
/*  53:    */       case 1: 
/*  54: 41 */         this.data.devices[SI3040.DevId50].Interrupt(1);
/*  55: 42 */         this.data.devices[PowerBoot.DevId10].Interrupt(196608); break;
/*  56:    */       case 2: 
/*  57: 43 */         this.data.devices[PowerBoot.DevId10].Interrupt(262144); break;
/*  58:    */       case 3: 
/*  59: 44 */         this.data.devices[PowerBoot.DevId10].Interrupt(393216); break;
/*  60:    */       case 4: 
/*  61: 45 */         this.data.devices[PowerBoot.DevId10].Interrupt(327680); break;
/*  62:    */       case 5: 
/*  63: 46 */         this.data.devices[PowerBoot.DevId10].Interrupt(524288); break;
/*  64:    */       case 6: 
/*  65: 47 */         this.data.devices[PowerBoot.DevId10].Interrupt(458752); break;
/*  66:    */       case 7: 
/*  67: 48 */         this.data.devices[PowerBoot.DevId10].Interrupt(589824);
/*  68:    */       }
/*  69:    */     }
/*  70: 51 */     switch (menu)
/*  71:    */     {
/*  72:    */     case 8: 
/*  73: 52 */       setSpeed(false); break;
/*  74:    */     case 9: 
/*  75: 53 */       setSpeed(true); break;
/*  76:    */     case 10: 
/*  77: 54 */       setStyle(true); break;
/*  78:    */     case 11: 
/*  79: 55 */       setStyle(false);
/*  80:    */     }
/*  81:    */   }
/*  82:    */   
/*  83:    */   public void setSpeed(boolean speed)
/*  84:    */   {
/*  85: 60 */     this.data.speed = speed;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public void setStyle(boolean style)
/*  89:    */   {
/*  90: 64 */     this.data.style = style;
/*  91:    */   }
/*  92:    */   
/*  93:    */   public void setPanel(Panel8 panel)
/*  94:    */   {
/*  95: 68 */     this.panel = panel;
/*  96:    */   }
/*  97:    */   
/*  98:    */   public void setSwitchReg(int value)
/*  99:    */   {
/* 100: 72 */     this.data.swr = value;
/* 101:    */   }
/* 102:    */   
/* 103:    */   public void setSelect(int value)
/* 104:    */   {
/* 105: 77 */     this.select = value;
/* 106: 78 */     report("Select", value);
/* 107:    */   }
/* 108:    */   
/* 109:    */   public void setPower(int value)
/* 110:    */   {
/* 111: 82 */     this.power = (value << 16);
/* 112: 83 */     report("Power", value);
/* 113: 84 */     if (this.data.devices[PowerBoot.DevId10] != null) {
/* 114: 85 */       this.data.devices[PowerBoot.DevId10].Interrupt(this.power);
/* 115: 87 */     } else if (this.power == 0) {
/* 116: 88 */       this.data.CloseAllDevs();
/* 117:    */     }
/* 118:    */   }
/* 119:    */   
/* 120:    */   public void setSw(boolean state)
/* 121:    */   {
/* 122: 94 */     this.data.sw = state;
/* 123: 95 */     report("Sw", state);
/* 124:    */   }
/* 125:    */   
/* 126:    */   public void setEal(boolean state)
/* 127:    */   {
/* 128: 99 */     if (!this.data.run)
/* 129:    */     {
/* 130:100 */       if (state)
/* 131:    */       {
/* 132:101 */         this.data.state = 1;
/* 133:102 */         this.data.ifr = ((this.data.swr & 0x38) >> 3);
/* 134:103 */         this.data.ema = (this.data.ifr << 12);
/* 135:104 */         this.data.dfr = (this.data.swr & 0x7);
/* 136:105 */         this.data.mmena = false;
/* 137:    */       }
/* 138:107 */       this.eal = state;
/* 139:108 */       report("Eal", state);
/* 140:    */     }
/* 141:    */   }
/* 142:    */   
/* 143:    */   public void setAl(boolean state)
/* 144:    */   {
/* 145:113 */     if (!this.data.run)
/* 146:    */     {
/* 147:114 */       if (state)
/* 148:    */       {
/* 149:115 */         this.data.state = 1;
/* 150:116 */         this.data.data = this.data.swr;
/* 151:117 */         this.data.cpma = this.data.data;
/* 152:118 */         this.data.ma = this.data.cpma;
/* 153:    */       }
/* 154:120 */       this.al = state;
/* 155:121 */       report("Al", state);
/* 156:    */     }
/* 157:    */   }
/* 158:    */   
/* 159:    */   public void setClear(boolean state)
/* 160:    */   {
/* 161:126 */     this.clear = state;
/* 162:127 */     if (!this.data.run)
/* 163:    */     {
/* 164:128 */       if (state)
/* 165:    */       {
/* 166:129 */         this.data.ac = 0;
/* 167:130 */         this.data.link = 0;
/* 168:131 */         this.data.ClearAllFlags();
/* 169:    */       }
/* 170:133 */       report("Clear", state);
/* 171:    */     }
/* 172:    */   }
/* 173:    */   
/* 174:    */   public void setCont(boolean state)
/* 175:    */   {
/* 176:138 */     this.cont = state;
/* 177:139 */     if (!this.data.run)
/* 178:    */     {
/* 179:140 */       if (state)
/* 180:    */       {
/* 181:141 */         this.data.msirdis = false;
/* 182:142 */         this.data.run = true;
/* 183:    */       }
/* 184:144 */       report("Cont", state);
/* 185:    */     }
/* 186:    */   }
/* 187:    */   
/* 188:    */   public void setExam(boolean state)
/* 189:    */   {
/* 190:149 */     if (!this.data.run)
/* 191:    */     {
/* 192:150 */       if (state)
/* 193:    */       {
/* 194:151 */         this.data.msirdis = true;
/* 195:152 */         this.data.keyctrl = true;
/* 196:153 */         this.data.bkdctrl = true;
/* 197:154 */         this.data.malctrl = false;
/* 198:155 */         this.data.mddir = false;
/* 199:156 */         this.data.run = true;
/* 200:    */       }
/* 201:158 */       this.exam = state;
/* 202:159 */       report("Exam", state);
/* 203:    */     }
/* 204:    */   }
/* 205:    */   
/* 206:    */   public void setHalt(boolean state)
/* 207:    */   {
/* 208:164 */     this.data.halt = state;
/* 209:165 */     report("Halt", state);
/* 210:    */   }
/* 211:    */   
/* 212:    */   public void setSingStep(boolean state)
/* 213:    */   {
/* 214:169 */     this.data.singstep = state;
/* 215:170 */     report("SingStep", state);
/* 216:    */   }
/* 217:    */   
/* 218:    */   public void setDep(boolean state)
/* 219:    */   {
/* 220:174 */     if (!this.data.run)
/* 221:    */     {
/* 222:175 */       if (state)
/* 223:    */       {
/* 224:176 */         this.data.msirdis = true;
/* 225:177 */         this.data.keyctrl = true;
/* 226:178 */         this.data.bkdctrl = false;
/* 227:179 */         this.data.malctrl = false;
/* 228:180 */         this.data.mddir = true;
/* 229:181 */         this.data.data = this.data.swr;
/* 230:182 */         this.data.run = true;
/* 231:    */       }
/* 232:184 */       this.dep = state;
/* 233:185 */       report("Dep", state);
/* 234:    */     }
/* 235:    */   }
/* 236:    */   
/* 237:    */   private void report(String name, boolean state) {}
/* 238:    */   
/* 239:    */   private void report(String name, int value) {}
/* 240:    */   
/* 241:    */   private int makeStatus()
/* 242:    */   {
/* 243:198 */     int status = 0;
/* 244:199 */     if (this.data.link == 1) {
/* 245:200 */       status |= 0x800;
/* 246:    */     }
/* 247:202 */     if (this.data.gtf > 0) {
/* 248:203 */       status |= 0x400;
/* 249:    */     }
/* 250:205 */     if (this.data.intreq > 0L) {
/* 251:206 */       status |= 0x200;
/* 252:    */     }
/* 253:208 */     if (this.data.intinhibit) {
/* 254:209 */       status |= 0x100;
/* 255:    */     }
/* 256:211 */     if (this.data.intena) {
/* 257:212 */       status |= 0x80;
/* 258:    */     }
/* 259:214 */     if (this.data.usermode) {
/* 260:215 */       status |= 0x40;
/* 261:    */     }
/* 262:217 */     status |= this.data.ifr << 3 & 0x38;
/* 263:218 */     status |= this.data.dfr & 0x7;
/* 264:219 */     return status;
/* 265:    */   }
/* 266:    */   
/* 267:    */   private int makeState()
/* 268:    */   {
/* 269:223 */     int status = 0;
/* 270:224 */     if (this.data.state == 1) {
/* 271:225 */       status |= 0x800;
/* 272:    */     }
/* 273:227 */     if (this.data.state == 2) {
/* 274:228 */       status |= 0x400;
/* 275:    */     }
/* 276:230 */     if (this.data.state == 3) {
/* 277:231 */       status |= 0x200;
/* 278:    */     }
/* 279:233 */     status |= this.data.ir << 6 & 0x1C0;
/* 280:234 */     if (!this.data.mddir) {
/* 281:235 */       status |= 0x20;
/* 282:    */     }
/* 283:237 */     if (this.data.bkdctrl) {
/* 284:238 */       status |= 0x10;
/* 285:    */     }
/* 286:240 */     if (this.data.sw) {
/* 287:241 */       status |= 0x8;
/* 288:    */     }
/* 289:243 */     if (this.data.iopause) {
/* 290:244 */       status |= 0x4;
/* 291:    */     }
/* 292:249 */     return status;
/* 293:    */   }
/* 294:    */   
/* 295:    */   public void run()
/* 296:    */   {
/* 297:    */     for (;;)
/* 298:    */     {
/* 299:255 */       this.panel.runLamp.setState(this.data.run);
/* 300:256 */       if (this.power < 2)
/* 301:    */       {
/* 302:257 */         switch (this.select)
/* 303:    */         {
/* 304:    */         case 0: 
/* 305:259 */           this.panel.busLamps.setState(this.data.data);
/* 306:260 */           break;
/* 307:    */         case 1: 
/* 308:262 */           this.panel.busLamps.setState(this.data.mq);
/* 309:263 */           break;
/* 310:    */         case 2: 
/* 311:265 */           this.panel.busLamps.setState(this.data.md);
/* 312:266 */           break;
/* 313:    */         case 3: 
/* 314:268 */           this.panel.busLamps.setState(this.data.ac);
/* 315:269 */           break;
/* 316:    */         case 4: 
/* 317:271 */           this.panel.busLamps.setState(makeStatus());
/* 318:272 */           break;
/* 319:    */         case 5: 
/* 320:274 */           this.panel.busLamps.setState(makeState());
/* 321:    */         }
/* 322:277 */         this.panel.maLamps.setState(this.data.ma);
/* 323:278 */         this.panel.emaLamps.setState(this.data.ema >> 12);
/* 324:279 */         if (!this.data.style)
/* 325:    */         {
/* 326:280 */           this.panel.stateLamps.setState(makeState());
/* 327:281 */           this.panel.statusLamps.setState(makeStatus());
/* 328:282 */           this.panel.acLamps.setState(this.data.ac);
/* 329:283 */           this.panel.mdLamps.setState(this.data.md);
/* 330:284 */           this.panel.mqLamps.setState(this.data.mq);
/* 331:285 */           this.panel.dataLamps.setState(this.data.data);
/* 332:    */         }
/* 333:    */         else
/* 334:    */         {
/* 335:287 */           this.panel.stateLamps.setState(0);
/* 336:288 */           this.panel.statusLamps.setState(0);
/* 337:289 */           this.panel.acLamps.setState(0);
/* 338:290 */           this.panel.mdLamps.setState(0);
/* 339:291 */           this.panel.mqLamps.setState(0);
/* 340:292 */           this.panel.dataLamps.setState(0);
/* 341:    */         }
/* 342:    */       }
/* 343:    */       else
/* 344:    */       {
/* 345:295 */         this.panel.busLamps.setState(0);
/* 346:296 */         this.panel.maLamps.setState(0);
/* 347:297 */         this.panel.emaLamps.setState(0);
/* 348:298 */         this.panel.stateLamps.setState(0);
/* 349:299 */         this.panel.statusLamps.setState(0);
/* 350:300 */         this.panel.acLamps.setState(0);
/* 351:301 */         this.panel.mdLamps.setState(0);
/* 352:302 */         this.panel.mqLamps.setState(0);
/* 353:303 */         this.panel.dataLamps.setState(0);
/* 354:    */       }
/* 355:    */       try
/* 356:    */       {
/* 357:306 */         Thread.sleep(20L);
/* 358:    */       }
/* 359:    */       catch (InterruptedException e) {}
/* 360:    */     }
/* 361:    */   }
/* 362:    */   
/* 363:    */   public void Booter(BootType boot)
/* 364:    */   {
/* 365:314 */     int[] memory = new int[''];
/* 366:320 */     if (this.data.devices[SI3040.DevId50] != null) {
/* 367:321 */       this.data.devices[SI3040.DevId50].Interrupt(0);
/* 368:    */     }
/* 369:324 */     switch (1.$SwitchMap$Logic$PanelLogic_backup$BootType[boot.ordinal()])
/* 370:    */     {
/* 371:    */     case 1: 
/* 372:326 */       if (this.data.devices[SI3040.DevId50] != null) {
/* 373:327 */         this.data.devices[SI3040.DevId50].Interrupt(1);
/* 374:    */       }
/* 375:    */     case 2: 
/* 376:331 */       codeOffset = 0;
/* 377:332 */       codeStart = 0;
/* 378:333 */       codeEnd = 5;
/* 379:334 */       start = 0;
/* 380:335 */       memory[0] = 3394;
/* 381:336 */       memory[1] = 0;
/* 382:337 */       memory[2] = 3407;
/* 383:338 */       memory[3] = 3402;
/* 384:339 */       memory[4] = 3404;
/* 385:340 */       memory[5] = 2565;
/* 386:341 */       break;
/* 387:    */     case 3: 
/* 388:343 */       codeOffset = 3712;
/* 389:344 */       codeStart = 64;
/* 390:345 */       codeEnd = 90;
/* 391:346 */       start = 3776;
/* 392:347 */       memory[64] = 714;
/* 393:348 */       memory[65] = 2250;
/* 394:349 */       memory[66] = 2250;
/* 395:350 */       memory[67] = 3579;
/* 396:351 */       memory[68] = 2755;
/* 397:352 */       memory[69] = 3583;
/* 398:353 */       memory[70] = 2006;
/* 399:354 */       memory[71] = 1238;
/* 400:355 */       memory[72] = 2755;
/* 401:356 */       memory[73] = 3034;
/* 402:357 */       memory[74] = 1024;
/* 403:358 */       memory[75] = 704;
/* 404:359 */       memory[76] = 3580;
/* 405:360 */       memory[77] = 3577;
/* 406:361 */       memory[78] = 2765;
/* 407:362 */       memory[79] = 3582;
/* 408:363 */       memory[80] = 217;
/* 409:364 */       memory[81] = 727;
/* 410:365 */       memory[82] = 4000;
/* 411:366 */       memory[83] = 2765;
/* 412:367 */       memory[84] = 1233;
/* 413:368 */       memory[85] = 3018;
/* 414:369 */       memory[86] = 3820;
/* 415:370 */       memory[87] = 4078;
/* 416:371 */       memory[88] = 4071;
/* 417:372 */       memory[89] = 63;
/* 418:373 */       memory[90] = 3840;
/* 419:374 */       break;
/* 420:    */     case 4: 
/* 421:376 */       memory[10] = 4095;
/* 422:    */     case 5: 
/* 423:379 */       codeOffset = 3968;
/* 424:380 */       codeStart = 10;
/* 425:381 */       codeEnd = 109;
/* 426:382 */       start = 4033;
/* 427:    */       
/* 428:384 */       memory[11] = 0;
/* 429:385 */       memory[12] = 0;
/* 430:386 */       memory[13] = 0;
/* 431:387 */       memory[14] = 0;
/* 432:388 */       memory[22] = 0;
/* 433:389 */       memory[23] = 1674;
/* 434:390 */       memory[24] = 2224;
/* 435:391 */       memory[25] = 704;
/* 436:392 */       memory[26] = 4072;
/* 437:393 */       memory[27] = 2719;
/* 438:394 */       memory[28] = 1162;
/* 439:395 */       memory[29] = 3616;
/* 440:396 */       memory[30] = 2711;
/* 441:397 */       memory[31] = 650;
/* 442:398 */       memory[32] = 4000;
/* 443:399 */       memory[33] = 2712;
/* 444:400 */       memory[34] = 652;
/* 445:401 */       memory[35] = 188;
/* 446:402 */       memory[36] = 737;
/* 447:403 */       memory[37] = 3912;
/* 448:404 */       memory[38] = 1174;
/* 449:405 */       memory[39] = 4072;
/* 450:406 */       memory[40] = 2966;
/* 451:407 */       memory[41] = 652;
/* 452:408 */       memory[42] = 174;
/* 453:409 */       memory[43] = 687;
/* 454:410 */       memory[44] = 1675;
/* 455:411 */       memory[45] = 2712;
/* 456:412 */       memory[46] = 56;
/* 457:413 */       memory[47] = 3201;
/* 458:414 */       memory[48] = 0;
/* 459:415 */       memory[49] = 0;
/* 460:416 */       memory[50] = 3097;
/* 461:417 */       memory[51] = 2738;
/* 462:418 */       memory[52] = 3102;
/* 463:419 */       memory[53] = 1676;
/* 464:420 */       memory[54] = 652;
/* 465:421 */       memory[55] = 2992;
/* 466:422 */       memory[56] = 3081;
/* 467:423 */       memory[57] = 2744;
/* 468:424 */       memory[58] = 3086;
/* 469:425 */       memory[59] = 2741;
/* 470:426 */       memory[60] = 192;
/* 471:427 */       memory[61] = 2275;
/* 472:428 */       memory[62] = 3617;
/* 473:429 */       memory[63] = 653;
/* 474:430 */       memory[64] = 3842;
/* 475:431 */       memory[65] = 3098;
/* 476:432 */       memory[66] = 3084;
/* 477:433 */       memory[67] = 3212;
/* 478:434 */       memory[68] = 687;
/* 479:435 */       memory[69] = 1675;
/* 480:436 */       memory[70] = 650;
/* 481:437 */       memory[71] = 4032;
/* 482:438 */       memory[72] = 747;
/* 483:439 */       memory[73] = 746;
/* 484:440 */       memory[74] = 1713;
/* 485:441 */       memory[75] = 2198;
/* 486:442 */       memory[76] = 2763;
/* 487:443 */       memory[77] = 1677;
/* 488:444 */       memory[78] = 651;
/* 489:445 */       memory[79] = 1758;
/* 490:446 */       memory[80] = 652;
/* 491:447 */       memory[81] = 1790;
/* 492:448 */       memory[82] = 2224;
/* 493:449 */       memory[83] = 1773;
/* 494:450 */       memory[84] = 2198;
/* 495:451 */       memory[85] = 2749;
/* 496:452 */       memory[86] = 2275;
/* 497:453 */       memory[87] = 3856;
/* 498:454 */       memory[88] = 2782;
/* 499:455 */       memory[89] = 1678;
/* 500:456 */       memory[90] = 766;
/* 501:457 */       memory[91] = 749;
/* 502:458 */       memory[92] = 653;
/* 503:459 */       memory[93] = 2765;
/* 504:460 */       memory[94] = 0;
/* 505:461 */       memory[95] = 1934;
/* 506:462 */       memory[96] = 1166;
/* 507:463 */       memory[97] = 3968;
/* 508:464 */       memory[98] = 2778;
/* 509:465 */       memory[99] = 0;
/* 510:466 */       memory[100] = 766;
/* 511:467 */       memory[101] = 3654;
/* 512:468 */       memory[102] = 3590;
/* 513:469 */       memory[103] = 3590;
/* 514:470 */       memory[104] = 749;
/* 515:471 */       memory[105] = 3043;
/* 516:472 */       memory[106] = 2738;
/* 517:473 */       memory[107] = 6;
/* 518:474 */       memory[108] = 0;
/* 519:475 */       memory[109] = 0;
/* 520:    */       
/* 521:477 */       break;
/* 522:    */     case 6: 
/* 523:479 */       codeOffset = 3968;
/* 524:480 */       codeStart = 110;
/* 525:481 */       codeEnd = 126;
/* 526:482 */       start = 4078;
/* 527:483 */       memory[110] = 3098;
/* 528:484 */       memory[111] = 3097;
/* 529:485 */       memory[112] = 2799;
/* 530:486 */       memory[113] = 3102;
/* 531:487 */       memory[114] = 3654;
/* 532:488 */       memory[115] = 3590;
/* 533:489 */       memory[116] = 3912;
/* 534:490 */       memory[117] = 2799;
/* 535:491 */       memory[118] = 3590;
/* 536:492 */       memory[119] = 3097;
/* 537:493 */       memory[120] = 2807;
/* 538:494 */       memory[121] = 3100;
/* 539:495 */       memory[122] = 3856;
/* 540:496 */       memory[123] = 2046;
/* 541:497 */       memory[124] = 1790;
/* 542:498 */       memory[125] = 2798;
/* 543:499 */       memory[126] = 0;
/* 544:500 */       break;
/* 545:    */     case 7: 
/* 546:502 */       codeOffset = 3968;
/* 547:503 */       codeStart = 110;
/* 548:504 */       codeEnd = 126;
/* 549:505 */       start = 4078;
/* 550:506 */       memory[110] = 3084;
/* 551:507 */       memory[111] = 3081;
/* 552:508 */       memory[112] = 2799;
/* 553:509 */       memory[113] = 3086;
/* 554:510 */       memory[114] = 3654;
/* 555:511 */       memory[115] = 3590;
/* 556:512 */       memory[116] = 3912;
/* 557:513 */       memory[117] = 2812;
/* 558:514 */       memory[118] = 3590;
/* 559:515 */       memory[119] = 3081;
/* 560:516 */       memory[120] = 2807;
/* 561:517 */       memory[121] = 3086;
/* 562:518 */       memory[122] = 3856;
/* 563:519 */       memory[123] = 2046;
/* 564:520 */       memory[124] = 1790;
/* 565:521 */       memory[125] = 2799;
/* 566:522 */       memory[126] = 0;
/* 567:523 */       break;
/* 568:    */     case 8: 
/* 569:525 */       codeOffset = 3840;
/* 570:526 */       codeStart = 53;
/* 571:527 */       codeEnd = 127;
/* 572:528 */       start = 3893;
/* 573:529 */       memory[53] = 764;
/* 574:530 */       memory[54] = 3094;
/* 575:531 */       memory[55] = 1782;
/* 576:532 */       memory[56] = 2264;
/* 577:533 */       memory[57] = 3842;
/* 578:534 */       memory[58] = 3972;
/* 579:535 */       memory[59] = 3617;
/* 580:536 */       memory[60] = 1783;
/* 581:537 */       memory[61] = 3842;
/* 582:538 */       memory[62] = 3972;
/* 583:539 */       memory[63] = 1784;
/* 584:540 */       memory[64] = 3842;
/* 585:541 */       memory[65] = 3972;
/* 586:542 */       memory[66] = 3585;
/* 587:543 */       memory[67] = 1785;
/* 588:544 */       memory[68] = 760;
/* 589:545 */       memory[69] = 3664;
/* 590:546 */       memory[70] = 2273;
/* 591:547 */       memory[71] = 760;
/* 592:548 */       memory[72] = 3617;
/* 593:549 */       memory[73] = 761;
/* 594:550 */       memory[74] = 4008;
/* 595:551 */       memory[75] = 2768;
/* 596:552 */       memory[76] = 1016;
/* 597:553 */       memory[77] = 3648;
/* 598:554 */       memory[78] = 1272;
/* 599:555 */       memory[79] = 2758;
/* 600:556 */       memory[80] = 1271;
/* 601:557 */       memory[81] = 2749;
/* 602:558 */       memory[82] = 758;
/* 603:559 */       memory[83] = 3648;
/* 604:560 */       memory[84] = 2273;
/* 605:561 */       memory[85] = 2264;
/* 606:562 */       memory[86] = 3842;
/* 607:563 */       memory[87] = 2741;
/* 608:564 */       memory[88] = 0;
/* 609:565 */       memory[89] = 3776;
/* 610:566 */       memory[90] = 762;
/* 611:567 */       memory[91] = 1787;
/* 612:568 */       memory[92] = 764;
/* 613:569 */       memory[93] = 2289;
/* 614:570 */       memory[94] = 1275;
/* 615:571 */       memory[95] = 2781;
/* 616:572 */       memory[96] = 3032;
/* 617:573 */       memory[97] = 0;
/* 618:574 */       memory[98] = 1789;
/* 619:575 */       memory[99] = 765;
/* 620:576 */       memory[100] = 3594;
/* 621:577 */       memory[101] = 3594;
/* 622:578 */       memory[102] = 3594;
/* 623:579 */       memory[103] = 254;
/* 624:580 */       memory[104] = 2289;
/* 625:581 */       memory[105] = 758;
/* 626:582 */       memory[106] = 1782;
/* 627:583 */       memory[107] = 765;
/* 628:584 */       memory[108] = 255;
/* 629:585 */       memory[109] = 2289;
/* 630:586 */       memory[110] = 758;
/* 631:587 */       memory[111] = 1782;
/* 632:588 */       memory[112] = 3041;
/* 633:589 */       memory[113] = 0;
/* 634:590 */       memory[114] = 3089;
/* 635:591 */       memory[115] = 2802;
/* 636:592 */       memory[116] = 3094;
/* 637:593 */       memory[117] = 3057;
/* 638:594 */       memory[118] = 0;
/* 639:595 */       memory[119] = 0;
/* 640:596 */       memory[120] = 0;
/* 641:597 */       memory[121] = 0;
/* 642:598 */       memory[122] = 3958;
/* 643:599 */       memory[123] = 0;
/* 644:600 */       memory[124] = 128;
/* 645:601 */       memory[125] = 0;
/* 646:602 */       memory[126] = 127;
/* 647:603 */       memory[127] = 63;
/* 648:604 */       break;
/* 649:    */     }
/* 650:606 */     int codeOffset = 0;
/* 651:607 */     int codeStart = 0;
/* 652:608 */     int codeEnd = 0;
/* 653:609 */     int start = 0;
/* 654:611 */     for (int i = codeStart; i <= codeEnd; i++) {
/* 655:612 */       this.data.memory[(i + codeOffset)] = memory[i];
/* 656:    */     }
/* 657:614 */     int swi = this.data.swr;
/* 658:615 */     setSwitchReg(0);
/* 659:616 */     setEal(true);
/* 660:617 */     setSwitchReg(start);
/* 661:618 */     setAl(true);
/* 662:619 */     setSwitchReg(swi);
/* 663:620 */     setClear(true);
/* 664:621 */     setCont(true);
/* 665:    */   }
/* 666:    */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Logic.PanelLogic_backup
 * JD-Core Version:    0.7.0.1
 */