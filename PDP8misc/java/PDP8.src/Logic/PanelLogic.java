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
/*  15:    */ public class PanelLogic
/*  16:    */   extends Thread
/*  17:    */   implements Constants
/*  18:    */ {
/*  19:    */   public BusRegMem data;
/*  20:    */   private Panel8 panel;
/*  21:    */   
/*  22:    */   public PanelLogic(BusRegMem data, String name)
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
/*  38:    */   public void menuContext(int menu)
/*  39:    */   {
/*  40: 32 */     if (this.data.devices[PowerBoot.DevId10] != null) {
/*  41: 33 */       switch (menu)
/*  42:    */       {
/*  43:    */       case 0: 
/*  44: 34 */         this.data.devices[SI3040.DevId50].Interrupt(0);
/*  45: 35 */         this.data.devices[PowerBoot.DevId10].Interrupt(196608); break;
/*  46:    */       case 1: 
/*  47: 36 */         this.data.devices[SI3040.DevId50].Interrupt(1);
/*  48: 37 */         this.data.devices[PowerBoot.DevId10].Interrupt(196608); break;
/*  49:    */       case 2: 
/*  50: 38 */         this.data.devices[PowerBoot.DevId10].Interrupt(262144); break;
/*  51:    */       case 3: 
/*  52: 39 */         this.data.devices[PowerBoot.DevId10].Interrupt(393216); break;
/*  53:    */       case 4: 
/*  54: 40 */         this.data.devices[PowerBoot.DevId10].Interrupt(327680); break;
/*  55:    */       case 5: 
/*  56: 41 */         this.data.devices[PowerBoot.DevId10].Interrupt(524288); break;
/*  57:    */       case 6: 
/*  58: 42 */         this.data.devices[PowerBoot.DevId10].Interrupt(458752); break;
/*  59:    */       case 7: 
/*  60: 43 */         this.data.devices[PowerBoot.DevId10].Interrupt(589824);
/*  61:    */       }
/*  62:    */     }
/*  63: 46 */     switch (menu)
/*  64:    */     {
/*  65:    */     case 8: 
/*  66: 47 */       setSpeed(false); break;
/*  67:    */     case 9: 
/*  68: 48 */       setSpeed(true); break;
/*  69:    */     case 10: 
/*  70: 49 */       setStyle(true); break;
/*  71:    */     case 11: 
/*  72: 50 */       setStyle(false);
/*  73:    */     }
/*  74:    */   }
/*  75:    */   
/*  76:    */   public void setSpeed(boolean speed)
/*  77:    */   {
/*  78: 55 */     this.data.speed = speed;
/*  79:    */   }
/*  80:    */   
/*  81:    */   public void setStyle(boolean style)
/*  82:    */   {
/*  83: 59 */     this.data.style = style;
/*  84:    */   }
/*  85:    */   
/*  86:    */   public void setPanel(Panel8 panel)
/*  87:    */   {
/*  88: 63 */     this.panel = panel;
/*  89:    */   }
/*  90:    */   
/*  91:    */   public void setSwitchReg(int value)
/*  92:    */   {
/*  93: 67 */     this.data.swr = value;
/*  94:    */   }
/*  95:    */   
/*  96:    */   public void setSelect(int value)
/*  97:    */   {
/*  98: 72 */     this.select = value;
/*  99: 73 */     report("Select", value);
/* 100:    */   }
/* 101:    */   
/* 102:    */   public void setPower(int value)
/* 103:    */   {
/* 104: 77 */     this.power = value;
/* 105: 78 */     report("Power", value);
/* 106: 79 */     if (this.data.devices[PowerBoot.DevId10] != null) {
/* 107: 80 */       this.data.devices[PowerBoot.DevId10].Interrupt(this.power << 16);
/* 108: 82 */     } else if (this.power == 0) {
/* 109: 83 */       this.data.CloseAllDevs();
/* 110:    */     }
/* 111:    */   }
/* 112:    */   
/* 113:    */   public void setSw(boolean state)
/* 114:    */   {
/* 115: 89 */     this.data.sw = state;
/* 116: 90 */     report("Sw", state);
/* 117:    */   }
/* 118:    */   
/* 119:    */   public void setEal(boolean state)
/* 120:    */   {
/* 121: 94 */     if (!this.data.run)
/* 122:    */     {
/* 123: 95 */       if (state)
/* 124:    */       {
/* 125: 96 */         this.data.state = 1;
/* 126: 97 */         this.data.ifr = ((this.data.swr & 0x38) >> 3);
/* 127: 98 */         this.data.ema = (this.data.ifr << 12);
/* 128: 99 */         this.data.dfr = (this.data.swr & 0x7);
/* 129:100 */         this.data.mmena = false;
/* 130:    */       }
/* 131:102 */       this.eal = state;
/* 132:103 */       report("Eal", state);
/* 133:    */     }
/* 134:    */   }
/* 135:    */   
/* 136:    */   public void setAl(boolean state)
/* 137:    */   {
/* 138:108 */     if (!this.data.run)
/* 139:    */     {
/* 140:109 */       if (state)
/* 141:    */       {
/* 142:110 */         this.data.state = 1;
/* 143:111 */         this.data.data = this.data.swr;
/* 144:112 */         this.data.cpma = this.data.data;
/* 145:113 */         this.data.ma = this.data.cpma;
/* 146:    */       }
/* 147:115 */       this.al = state;
/* 148:116 */       report("Al", state);
/* 149:    */     }
/* 150:    */   }
/* 151:    */   
/* 152:    */   public void setClear(boolean state)
/* 153:    */   {
/* 154:121 */     this.clear = state;
/* 155:122 */     if (!this.data.run)
/* 156:    */     {
/* 157:123 */       if (state)
/* 158:    */       {
/* 159:124 */         this.data.ac = 0;
/* 160:125 */         this.data.link = 0;
/* 161:126 */         this.data.ClearAllFlags();
/* 162:    */       }
/* 163:128 */       report("Clear", state);
/* 164:    */     }
/* 165:    */   }
/* 166:    */   
/* 167:    */   public void setCont(boolean state)
/* 168:    */   {
/* 169:133 */     this.cont = state;
/* 170:134 */     if (!this.data.run)
/* 171:    */     {
/* 172:135 */       if (state)
/* 173:    */       {
/* 174:136 */         this.data.msirdis = false;
/* 175:137 */         this.data.run = true;
/* 176:    */       }
/* 177:139 */       report("Cont", state);
/* 178:    */     }
/* 179:    */   }
/* 180:    */   
/* 181:    */   public void setExam(boolean state)
/* 182:    */   {
/* 183:144 */     if (!this.data.run)
/* 184:    */     {
/* 185:145 */       if (state)
/* 186:    */       {
/* 187:146 */         this.data.msirdis = true;
/* 188:147 */         this.data.keyctrl = true;
/* 189:148 */         this.data.bkdctrl = true;
/* 190:149 */         this.data.malctrl = false;
/* 191:150 */         this.data.mddir = false;
/* 192:151 */         this.data.run = true;
/* 193:    */       }
/* 194:153 */       this.exam = state;
/* 195:154 */       report("Exam", state);
/* 196:    */     }
/* 197:    */   }
/* 198:    */   
/* 199:    */   public void setHalt(boolean state)
/* 200:    */   {
/* 201:159 */     this.data.halt = state;
/* 202:160 */     report("Halt", state);
/* 203:    */   }
/* 204:    */   
/* 205:    */   public void setSingStep(boolean state)
/* 206:    */   {
/* 207:164 */     this.data.singstep = state;
/* 208:165 */     report("SingStep", state);
/* 209:    */   }
/* 210:    */   
/* 211:    */   public void setDep(boolean state)
/* 212:    */   {
/* 213:169 */     if (!this.data.run)
/* 214:    */     {
/* 215:170 */       if (state)
/* 216:    */       {
/* 217:171 */         this.data.msirdis = true;
/* 218:172 */         this.data.keyctrl = true;
/* 219:173 */         this.data.bkdctrl = false;
/* 220:174 */         this.data.malctrl = false;
/* 221:175 */         this.data.mddir = true;
/* 222:176 */         this.data.data = this.data.swr;
/* 223:177 */         this.data.run = true;
/* 224:    */       }
/* 225:179 */       this.dep = state;
/* 226:180 */       report("Dep", state);
/* 227:    */     }
/* 228:    */   }
/* 229:    */   
/* 230:    */   private void report(String name, boolean state) {}
/* 231:    */   
/* 232:    */   private void report(String name, int value) {}
/* 233:    */   
/* 234:    */   private int makeStatus()
/* 235:    */   {
/* 236:193 */     int status = 0;
/* 237:194 */     if (this.data.link == 1) {
/* 238:195 */       status |= 0x800;
/* 239:    */     }
/* 240:197 */     if (this.data.gtf > 0) {
/* 241:198 */       status |= 0x400;
/* 242:    */     }
/* 243:200 */     if (this.data.intreq > 0L) {
/* 244:201 */       status |= 0x200;
/* 245:    */     }
/* 246:203 */     if (this.data.intinhibit) {
/* 247:204 */       status |= 0x100;
/* 248:    */     }
/* 249:206 */     if (this.data.intena) {
/* 250:207 */       status |= 0x80;
/* 251:    */     }
/* 252:209 */     if (this.data.usermode) {
/* 253:210 */       status |= 0x40;
/* 254:    */     }
/* 255:212 */     status |= this.data.ifr << 3 & 0x38;
/* 256:213 */     status |= this.data.dfr & 0x7;
/* 257:214 */     return status;
/* 258:    */   }
/* 259:    */   
/* 260:    */   private int makeState()
/* 261:    */   {
/* 262:218 */     int status = 0;
/* 263:219 */     if (this.data.state == 1) {
/* 264:220 */       status |= 0x800;
/* 265:    */     }
/* 266:222 */     if (this.data.state == 2) {
/* 267:223 */       status |= 0x400;
/* 268:    */     }
/* 269:225 */     if (this.data.state == 3) {
/* 270:226 */       status |= 0x200;
/* 271:    */     }
/* 272:228 */     status |= this.data.ir << 6 & 0x1C0;
/* 273:229 */     if (!this.data.mddir) {
/* 274:230 */       status |= 0x20;
/* 275:    */     }
/* 276:232 */     if (this.data.bkdctrl) {
/* 277:233 */       status |= 0x10;
/* 278:    */     }
/* 279:235 */     if (this.data.sw) {
/* 280:236 */       status |= 0x8;
/* 281:    */     }
/* 282:238 */     if (this.data.iopause) {
/* 283:239 */       status |= 0x4;
/* 284:    */     }
/* 285:244 */     return status;
/* 286:    */   }
/* 287:    */   
/* 288:    */   public void run()
/* 289:    */   {
/* 290:    */     for (;;)
/* 291:    */     {
/* 292:250 */       this.panel.runLamp.setState(this.data.run);
/* 293:251 */       if (this.power < 2)
/* 294:    */       {
/* 295:252 */         switch (this.select)
/* 296:    */         {
/* 297:    */         case 0: 
/* 298:254 */           this.panel.busLamps.setState(this.data.data);
/* 299:255 */           break;
/* 300:    */         case 1: 
/* 301:257 */           this.panel.busLamps.setState(this.data.mq);
/* 302:258 */           break;
/* 303:    */         case 2: 
/* 304:260 */           this.panel.busLamps.setState(this.data.md);
/* 305:261 */           break;
/* 306:    */         case 3: 
/* 307:263 */           this.panel.busLamps.setState(this.data.ac);
/* 308:264 */           break;
/* 309:    */         case 4: 
/* 310:266 */           this.panel.busLamps.setState(makeStatus());
/* 311:267 */           break;
/* 312:    */         case 5: 
/* 313:269 */           this.panel.busLamps.setState(makeState());
/* 314:    */         }
/* 315:272 */         this.panel.maLamps.setState(this.data.ma);
/* 316:273 */         this.panel.emaLamps.setState(this.data.ema >> 12);
/* 317:274 */         if (!this.data.style)
/* 318:    */         {
/* 319:275 */           this.panel.stateLamps.setState(makeState());
/* 320:276 */           this.panel.statusLamps.setState(makeStatus());
/* 321:277 */           this.panel.acLamps.setState(this.data.ac);
/* 322:278 */           this.panel.mdLamps.setState(this.data.md);
/* 323:279 */           this.panel.mqLamps.setState(this.data.mq);
/* 324:280 */           this.panel.dataLamps.setState(this.data.data);
/* 325:    */         }
/* 326:    */         else
/* 327:    */         {
/* 328:282 */           this.panel.stateLamps.setState(0);
/* 329:283 */           this.panel.statusLamps.setState(0);
/* 330:284 */           this.panel.acLamps.setState(0);
/* 331:285 */           this.panel.mdLamps.setState(0);
/* 332:286 */           this.panel.mqLamps.setState(0);
/* 333:287 */           this.panel.dataLamps.setState(0);
/* 334:    */         }
/* 335:    */       }
/* 336:    */       else
/* 337:    */       {
/* 338:290 */         this.panel.busLamps.setState(0);
/* 339:291 */         this.panel.maLamps.setState(0);
/* 340:292 */         this.panel.emaLamps.setState(0);
/* 341:293 */         this.panel.stateLamps.setState(0);
/* 342:294 */         this.panel.statusLamps.setState(0);
/* 343:295 */         this.panel.acLamps.setState(0);
/* 344:296 */         this.panel.mdLamps.setState(0);
/* 345:297 */         this.panel.mqLamps.setState(0);
/* 346:298 */         this.panel.dataLamps.setState(0);
/* 347:    */       }
/* 348:    */       try
/* 349:    */       {
/* 350:301 */         Thread.sleep(20L);
/* 351:    */       }
/* 352:    */       catch (InterruptedException e) {}
/* 353:    */     }
/* 354:    */   }
/* 355:    */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Logic.PanelLogic
 * JD-Core Version:    0.7.0.1
 */