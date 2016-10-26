/*   1:    */ package Logic;
/*   2:    */ 
/*   3:    */ import java.io.BufferedInputStream;
/*   4:    */ import java.io.BufferedOutputStream;
/*   5:    */ import java.io.File;
/*   6:    */ import java.io.FileInputStream;
/*   7:    */ import java.io.FileOutputStream;
/*   8:    */ import java.io.IOException;
/*   9:    */ import java.io.InputStream;
/*  10:    */ import java.io.OutputStream;
/*  11:    */ import java.io.PrintStream;
/*  12:    */ import java.util.Calendar;
/*  13:    */ import java.util.Properties;
/*  14:    */ 
/*  15:    */ public class BusRegMem
/*  16:    */   implements Constants
/*  17:    */ {
/*  18:    */   public long pdp8time;
/*  19:    */   public long pdp8inst;
/*  20:    */   public VirQueue virqueue;
/*  21:    */   
/*  22:    */   public BusRegMem()
/*  23:    */   {
/*  24: 21 */     this.fprops = new File("/PDP8.properties");
/*  25: 22 */     this.props = new Properties();
/*  26: 23 */     if (this.fprops.exists()) {
/*  27:    */       try
/*  28:    */       {
/*  29: 25 */         InputStream in = new BufferedInputStream(new FileInputStream(this.fprops.getPath()));
/*  30:    */         
/*  31: 27 */         this.props.load(in);
/*  32: 28 */         in.close();
/*  33:    */       }
/*  34:    */       catch (IOException e) {}
/*  35:    */     }
/*  36: 32 */     this.props.put("init", "initial entry");
/*  37:    */     
/*  38: 34 */     this.memory = new int[32768];
/*  39: 35 */     this.devices = new Device[64];
/*  40: 36 */     this.virqueue = new VirQueue(this);
/*  41: 37 */     this.devices[0] = new ProcMemIOTs(this);
/*  42: 38 */     this.eae = new EAE(this);
/*  43: 39 */     this.devices[PC8E.DevId01] = new PC8E(this);
/*  44: 40 */     this.devices[PC8E.DevId02] = this.devices[PC8E.DevId01];
/*  45: 41 */     this.devices[TTY.DevId03] = new TTY(this);
/*  46: 42 */     this.devices[TTY.DevId04] = this.devices[TTY.DevId03];
/*  47: 43 */     this.devices[PowerBoot.DevId10] = new PowerBoot(this);
/*  48: 44 */     this.devices[DK8EP.DevId13] = new DK8EP(this);
/*  49: 45 */     this.devices[14] = this.devices[0];
/*  50: 46 */     this.devices[15] = this.devices[0];
/*  51: 47 */     this.devices[16] = this.devices[0];
/*  52: 48 */     this.devices[17] = this.devices[0];
/*  53: 49 */     this.devices[18] = this.devices[0];
/*  54: 50 */     this.devices[19] = this.devices[0];
/*  55: 51 */     this.devices[20] = this.devices[0];
/*  56: 52 */     this.devices[21] = this.devices[0];
/*  57: 53 */     this.devices[22] = this.devices[0];
/*  58: 54 */     this.devices[23] = this.devices[0];
/*  59: 55 */     this.devices[SI3040.DevId50] = new SI3040(this);
/*  60: 56 */     this.devices[SI3040.DevId51] = this.devices[SI3040.DevId50];
/*  61: 57 */     this.devices[FPP.DevId55] = new FPP(this);
/*  62: 58 */     this.devices[FPP.DevId56] = this.devices[FPP.DevId55];
/*  63: 59 */     this.devices[LPT.DevId57] = new LPT(this);
/*  64: 60 */     this.devices[LPT.DevId66] = this.devices[LPT.DevId57];
/*  65: 61 */     this.devices[TD8E.DevId77] = new TD8E(this);
/*  66:    */     
/*  67: 63 */     this.ma = (this.cpma = this.pc = 4095);
/*  68:    */     
/*  69: 65 */     this.memory[''] = 3712;
/*  70: 66 */     this.memory[''] = 649;
/*  71: 67 */     this.memory[''] = 138;
/*  72: 68 */     this.memory[''] = 3110;
/*  73: 69 */     this.memory[''] = 3105;
/*  74: 70 */     this.memory[''] = 2692;
/*  75: 71 */     this.memory[''] = 1161;
/*  76: 72 */     this.memory[''] = 2688;
/*  77: 73 */     this.memory[''] = 2688;
/*  78: 74 */     this.memory[''] = 0;
/*  79: 75 */     this.memory[''] = 127;
/*  80:    */     
/*  81: 77 */     this.memory[256] = 3712;
/*  82: 78 */     this.memory[257] = 658;
/*  83: 79 */     this.memory[258] = 1680;
/*  84: 80 */     this.memory[259] = 659;
/*  85: 81 */     this.memory[260] = 1681;
/*  86: 82 */     this.memory[261] = 1169;
/*  87: 83 */     this.memory[262] = 2693;
/*  88: 84 */     this.memory[263] = 1168;
/*  89: 85 */     this.memory[264] = 2691;
/*  90: 86 */     this.memory[265] = 1172;
/*  91: 87 */     this.memory[266] = 660;
/*  92: 88 */     this.memory[267] = 3857;
/*  93: 89 */     this.memory[268] = 2688;
/*  94: 90 */     this.memory[272] = 0;
/*  95: 91 */     this.memory[273] = 0;
/*  96: 92 */     this.memory[274] = 3096;
/*  97: 93 */     this.memory[275] = 3833;
/*  98: 94 */     this.memory[276] = 0;
/*  99:    */     
/* 100: 96 */     this.memory[384] = 3098;
/* 101: 97 */     this.memory[385] = 3097;
/* 102: 98 */     this.memory[386] = 2689;
/* 103: 99 */     this.memory[387] = 3102;
/* 104:100 */     this.memory[388] = 3110;
/* 105:101 */     this.memory[389] = 3105;
/* 106:102 */     this.memory[390] = 2693;
/* 107:103 */     this.memory[391] = 3584;
/* 108:104 */     this.memory[392] = 2689;
/* 109:105 */     this.memory[393] = 2689;
/* 110:106 */     this.memory[394] = 255;
/* 111:    */     
/* 112:108 */     this.memory[512] = 585;
/* 113:109 */     this.memory[513] = 1;
/* 114:110 */     this.memory[514] = 2;
/* 115:111 */     this.memory[515] = 3;
/* 116:112 */     this.memory[516] = 4;
/* 117:113 */     this.memory[517] = 2;
/* 118:114 */     this.memory[518] = 1608;
/* 119:115 */     this.memory[519] = 2029;
/* 120:116 */     this.memory[520] = 1297;
/* 121:117 */     this.memory[521] = 180;
/* 122:118 */     this.memory[522] = 1550;
/* 123:    */     
/* 124:120 */     this.memory[528] = 585;
/* 125:121 */     this.memory[529] = 1;
/* 126:122 */     this.memory[530] = 2;
/* 127:123 */     this.memory[531] = 3;
/* 128:124 */     this.memory[532] = 4;
/* 129:125 */     this.memory[533] = 2;
/* 130:126 */     this.memory[534] = 2487;
/* 131:127 */     this.memory[535] = 2066;
/* 132:128 */     this.memory[536] = 2798;
/* 133:129 */     this.memory[537] = 3915;
/* 134:130 */     this.memory[538] = 2546;
/* 135:    */     
/* 136:132 */     this.memory[544] = 1;
/* 137:133 */     this.memory[545] = 1024;
/* 138:134 */     this.memory[546] = 0;
/* 139:135 */     this.memory[547] = 0;
/* 140:136 */     this.memory[548] = 0;
/* 141:137 */     this.memory[549] = 0;
/* 142:    */     
/* 143:139 */     Calendar today = Calendar.getInstance();
/* 144:140 */     int year = today.get(1) - 28 - 1970;
/* 145:141 */     this.memory[4095] = (year / 8 << 7);
/* 146:142 */     this.memory[8118] = ((today.get(2) + 1 << 8) + (today.get(5) << 3) + year % 8);
/* 147:    */     
/* 148:    */ 
/* 149:    */ 
/* 150:146 */     long st = System.currentTimeMillis();
/* 151:147 */     float k = 1.0F;
/* 152:148 */     long loops = 10000000L;
/* 153:149 */     for (long i = 0L; i < loops; i += 1L) {
/* 154:150 */       k += k;
/* 155:    */     }
/* 156:152 */     this.looptime = ((float)(System.currentTimeMillis() - st) * 1000.0F / (float)loops);
/* 157:153 */     System.out.println("PDP8 Emulator. Version: 2.95 on " + System.getProperty("os.name"));
/* 158:154 */     System.out.println("Computer float add time " + Math.floor(this.looptime * 1000000.0D) / 1000.0D + " ns");
/* 159:    */   }
/* 160:    */   
/* 161:163 */   public int ma = 0;
/* 162:164 */   public int ema = 0;
/* 163:165 */   public int data = 0;
/* 164:166 */   public int md = 0;
/* 165:167 */   public int swr = 0;
/* 166:168 */   public int ir = 0;
/* 167:169 */   public int state = 1;
/* 168:170 */   public int fset = 1;
/* 169:172 */   public boolean run = false;
/* 170:173 */   public boolean mddir = false;
/* 171:174 */   public boolean msirdis = false;
/* 172:175 */   public boolean keyctrl = false;
/* 173:176 */   public boolean bkdctrl = false;
/* 174:177 */   public boolean malctrl = false;
/* 175:178 */   public boolean intena = false;
/* 176:179 */   public boolean intdelay = false;
/* 177:180 */   public boolean intinprog = false;
/* 178:181 */   public long intreq = 0L;
/* 179:182 */   public boolean skip = false;
/* 180:183 */   public boolean sw = false;
/* 181:184 */   public boolean iopause = false;
/* 182:185 */   public boolean lastxfer = true;
/* 183:186 */   public boolean busstrobe = false;
/* 184:187 */   public boolean skipbus = false;
/* 185:188 */   public int c0c1c2 = 0;
/* 186:189 */   public boolean usermode = false;
/* 187:192 */   public int ac = 0;
/* 188:193 */   public int link = 0;
/* 189:194 */   public int mq = 0;
/* 190:    */   public int pc;
/* 191:    */   public int cpma;
/* 192:    */   public int mb;
/* 193:    */   public EAE eae;
/* 194:    */   public boolean modeb;
/* 195:    */   public int sc;
/* 196:    */   public int gtf;
/* 197:206 */   public int ifr = 0;
/* 198:207 */   public int dfr = 0;
/* 199:208 */   public boolean intinhibit = false;
/* 200:210 */   public boolean mmena = false;
/* 201:213 */   public boolean singstep = false;
/* 202:214 */   public boolean halt = false;
/* 203:    */   public int[] memory;
/* 204:    */   public float looptime;
/* 205:221 */   public boolean speed = true;
/* 206:225 */   public boolean style = true;
/* 207:    */   public Device[] devices;
/* 208:    */   public PowerBoot pwb;
/* 209:    */   public File fprops;
/* 210:    */   public Properties props;
/* 211:    */   
/* 212:    */   public void setProp(String key, String value)
/* 213:    */   {
/* 214:236 */     this.props.put(key, value);
/* 215:    */   }
/* 216:    */   
/* 217:    */   public void setProp(String key)
/* 218:    */   {
/* 219:240 */     this.props.remove(key);
/* 220:    */   }
/* 221:    */   
/* 222:    */   public String getProp(String key)
/* 223:    */   {
/* 224:244 */     return (String)this.props.get(key);
/* 225:    */   }
/* 226:    */   
/* 227:    */   public void setIntReq(int devid, boolean intreq)
/* 228:    */   {
/* 229:248 */     if (intreq) {
/* 230:249 */       this.intreq |= 1 << devid;
/* 231:    */     } else {
/* 232:251 */       this.intreq &= (1 << devid ^ 0xFFFFFFFF);
/* 233:    */     }
/* 234:    */   }
/* 235:    */   
/* 236:    */   public void setInterruptOff()
/* 237:    */   {
/* 238:256 */     this.intena = false;
/* 239:257 */     this.intdelay = false;
/* 240:258 */     this.intinprog = false;
/* 241:    */   }
/* 242:    */   
/* 243:    */   public void ClearAllFlags()
/* 244:    */   {
/* 245:262 */     for (int devc = 0; devc <= 63; devc++) {
/* 246:263 */       if (this.devices[devc] != null) {
/* 247:263 */         this.devices[devc].ClearFlags(devc);
/* 248:    */       }
/* 249:    */     }
/* 250:265 */     this.modeb = false;
/* 251:266 */     this.gtf = 0;
/* 252:    */   }
/* 253:    */   
/* 254:    */   public void ClearAllRun(boolean run)
/* 255:    */   {
/* 256:270 */     for (int devc = 0; devc <= 63; devc++) {
/* 257:271 */       if (this.devices[devc] != null) {
/* 258:271 */         this.devices[devc].ClearRun(run);
/* 259:    */       }
/* 260:    */     }
/* 261:273 */     this.modeb = false;
/* 262:274 */     this.gtf = 0;
/* 263:    */   }
/* 264:    */   
/* 265:    */   public void CloseAllDevs()
/* 266:    */   {
/* 267:278 */     for (int devc = 0; devc <= 63; devc++) {
/* 268:279 */       if (this.devices[devc] != null) {
/* 269:279 */         this.devices[devc].CloseDev(devc);
/* 270:    */       }
/* 271:    */     }
/* 272:    */     try
/* 273:    */     {
/* 274:282 */       OutputStream out = new BufferedOutputStream(new FileOutputStream(this.fprops.getPath()));
/* 275:    */       
/* 276:284 */       this.props.store(out, "PDP8 Properties");
/* 277:285 */       out.close();
/* 278:    */     }
/* 279:    */     catch (IOException e) {}
/* 280:288 */     System.exit(0);
/* 281:    */   }
/* 282:    */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Logic.BusRegMem
 * JD-Core Version:    0.7.0.1
 */