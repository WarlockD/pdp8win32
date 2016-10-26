/*   1:    */ package Panel;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Color;
/*   5:    */ import java.awt.Dimension;
/*   6:    */ import java.awt.Font;
/*   7:    */ import java.awt.GridBagConstraints;
/*   8:    */ import java.awt.GridBagLayout;
/*   9:    */ import java.awt.event.ComponentAdapter;
/*  10:    */ import java.awt.event.ComponentEvent;
/*  11:    */ import java.awt.event.MouseAdapter;
/*  12:    */ import java.awt.event.MouseEvent;
/*  13:    */ import java.beans.PropertyChangeEvent;
/*  14:    */ import java.beans.PropertyChangeListener;
/*  15:    */ import java.beans.PropertyChangeSupport;
/*  16:    */ import javax.swing.JLabel;
/*  17:    */ import javax.swing.JPanel;
/*  18:    */ import javax.swing.border.CompoundBorder;
/*  19:    */ import javax.swing.border.LineBorder;
/*  20:    */ 
/*  21:    */ public class SwitchReg
/*  22:    */   extends JPanel
/*  23:    */ {
/*  24:    */   private JLabel jLabel1;
/*  25:    */   private JPanel jPanel6;
/*  26:    */   private Switch swr0;
/*  27:    */   private Switch swr1;
/*  28:    */   private Switch swr10;
/*  29:    */   private Switch swr11;
/*  30:    */   private Switch swr2;
/*  31:    */   private Switch swr3;
/*  32:    */   private Switch swr4;
/*  33:    */   private Switch swr5;
/*  34:    */   private Switch swr6;
/*  35:    */   private Switch swr7;
/*  36:    */   private Switch swr8;
/*  37:    */   private Switch swr9;
/*  38:    */   private JLabel text0;
/*  39:    */   private JLabel text1;
/*  40:    */   private JLabel text10;
/*  41:    */   private JLabel text11;
/*  42:    */   private JLabel text2;
/*  43:    */   private JLabel text3;
/*  44:    */   private JLabel text4;
/*  45:    */   private JLabel text5;
/*  46:    */   private JLabel text6;
/*  47:    */   private JLabel text7;
/*  48:    */   private JLabel text8;
/*  49:    */   private JLabel text9;
/*  50:    */   private int swreg;
/*  51:    */   protected transient PropertyChangeSupport swrlisteners;
/*  52:    */   
/*  53:    */   public SwitchReg()
/*  54:    */   {
/*  55: 22 */     initComponents();
/*  56: 23 */     this.swreg = 0;
/*  57: 24 */     this.swrlisteners = new PropertyChangeSupport(this);
/*  58:    */     
/*  59: 26 */     this.swr0.addPropertyChangeListener(new PropertyChangeListener()
/*  60:    */     {
/*  61:    */       public void propertyChange(PropertyChangeEvent evt)
/*  62:    */       {
/*  63: 28 */         SwitchReg.this.swrPropertyChange(evt);
/*  64:    */       }
/*  65: 30 */     });
/*  66: 31 */     this.swr1.addPropertyChangeListener(new PropertyChangeListener()
/*  67:    */     {
/*  68:    */       public void propertyChange(PropertyChangeEvent evt)
/*  69:    */       {
/*  70: 33 */         SwitchReg.this.swrPropertyChange(evt);
/*  71:    */       }
/*  72: 35 */     });
/*  73: 36 */     this.swr2.addPropertyChangeListener(new PropertyChangeListener()
/*  74:    */     {
/*  75:    */       public void propertyChange(PropertyChangeEvent evt)
/*  76:    */       {
/*  77: 38 */         SwitchReg.this.swrPropertyChange(evt);
/*  78:    */       }
/*  79: 40 */     });
/*  80: 41 */     this.swr3.addPropertyChangeListener(new PropertyChangeListener()
/*  81:    */     {
/*  82:    */       public void propertyChange(PropertyChangeEvent evt)
/*  83:    */       {
/*  84: 43 */         SwitchReg.this.swrPropertyChange(evt);
/*  85:    */       }
/*  86: 45 */     });
/*  87: 46 */     this.swr4.addPropertyChangeListener(new PropertyChangeListener()
/*  88:    */     {
/*  89:    */       public void propertyChange(PropertyChangeEvent evt)
/*  90:    */       {
/*  91: 48 */         SwitchReg.this.swrPropertyChange(evt);
/*  92:    */       }
/*  93: 50 */     });
/*  94: 51 */     this.swr5.addPropertyChangeListener(new PropertyChangeListener()
/*  95:    */     {
/*  96:    */       public void propertyChange(PropertyChangeEvent evt)
/*  97:    */       {
/*  98: 53 */         SwitchReg.this.swrPropertyChange(evt);
/*  99:    */       }
/* 100: 55 */     });
/* 101: 56 */     this.swr6.addPropertyChangeListener(new PropertyChangeListener()
/* 102:    */     {
/* 103:    */       public void propertyChange(PropertyChangeEvent evt)
/* 104:    */       {
/* 105: 58 */         SwitchReg.this.swrPropertyChange(evt);
/* 106:    */       }
/* 107: 60 */     });
/* 108: 61 */     this.swr7.addPropertyChangeListener(new PropertyChangeListener()
/* 109:    */     {
/* 110:    */       public void propertyChange(PropertyChangeEvent evt)
/* 111:    */       {
/* 112: 63 */         SwitchReg.this.swrPropertyChange(evt);
/* 113:    */       }
/* 114: 65 */     });
/* 115: 66 */     this.swr8.addPropertyChangeListener(new PropertyChangeListener()
/* 116:    */     {
/* 117:    */       public void propertyChange(PropertyChangeEvent evt)
/* 118:    */       {
/* 119: 68 */         SwitchReg.this.swrPropertyChange(evt);
/* 120:    */       }
/* 121: 70 */     });
/* 122: 71 */     this.swr9.addPropertyChangeListener(new PropertyChangeListener()
/* 123:    */     {
/* 124:    */       public void propertyChange(PropertyChangeEvent evt)
/* 125:    */       {
/* 126: 73 */         SwitchReg.this.swrPropertyChange(evt);
/* 127:    */       }
/* 128: 75 */     });
/* 129: 76 */     this.swr10.addPropertyChangeListener(new PropertyChangeListener()
/* 130:    */     {
/* 131:    */       public void propertyChange(PropertyChangeEvent evt)
/* 132:    */       {
/* 133: 78 */         SwitchReg.this.swrPropertyChange(evt);
/* 134:    */       }
/* 135: 80 */     });
/* 136: 81 */     this.swr11.addPropertyChangeListener(new PropertyChangeListener()
/* 137:    */     {
/* 138:    */       public void propertyChange(PropertyChangeEvent evt)
/* 139:    */       {
/* 140: 83 */         SwitchReg.this.swrPropertyChange(evt);
/* 141:    */       }
/* 142:    */     });
/* 143:    */   }
/* 144:    */   
/* 145:    */   private void initComponents()
/* 146:    */   {
/* 147: 97 */     this.jPanel6 = new JPanel();
/* 148: 98 */     this.swr10 = new Switch();
/* 149: 99 */     this.text10 = new JLabel();
/* 150:100 */     this.swr9 = new Switch();
/* 151:101 */     this.text9 = new JLabel();
/* 152:102 */     this.swr11 = new Switch();
/* 153:103 */     this.text11 = new JLabel();
/* 154:104 */     this.text6 = new JLabel();
/* 155:105 */     this.text7 = new JLabel();
/* 156:106 */     this.text8 = new JLabel();
/* 157:107 */     this.swr6 = new Switch();
/* 158:108 */     this.swr7 = new Switch();
/* 159:109 */     this.swr8 = new Switch();
/* 160:110 */     this.text0 = new JLabel();
/* 161:111 */     this.text1 = new JLabel();
/* 162:112 */     this.text2 = new JLabel();
/* 163:113 */     this.swr0 = new Switch();
/* 164:114 */     this.swr1 = new Switch();
/* 165:115 */     this.swr2 = new Switch();
/* 166:116 */     this.text3 = new JLabel();
/* 167:117 */     this.text4 = new JLabel();
/* 168:118 */     this.text5 = new JLabel();
/* 169:119 */     this.swr3 = new Switch();
/* 170:120 */     this.swr4 = new Switch();
/* 171:121 */     this.swr5 = new Switch();
/* 172:122 */     this.jLabel1 = new JLabel();
/* 173:    */     
/* 174:124 */     setLayout(new BorderLayout());
/* 175:    */     
/* 176:126 */     this.jPanel6.setLayout(new GridBagLayout());
/* 177:    */     
/* 178:128 */     this.jPanel6.setBackground(new Color(204, 204, 204));
/* 179:129 */     this.jPanel6.setBorder(new CompoundBorder(new LineBorder(new Color(255, 255, 255)), new CompoundBorder(new LineBorder(new Color(0, 0, 0)), new LineBorder(new Color(255, 255, 255)))));
/* 180:130 */     this.jPanel6.setMaximumSize(new Dimension(318, 72));
/* 181:131 */     this.jPanel6.setMinimumSize(new Dimension(159, 36));
/* 182:132 */     this.swr10.setInitial(false);
/* 183:133 */     this.swr10.setMaximumSize(new Dimension(26, 44));
/* 184:134 */     this.swr10.setName("1");
/* 185:135 */     this.swr10.setToggle(true);
/* 186:136 */     this.swr10.setTruepos(false);
/* 187:137 */     GridBagConstraints gridBagConstraints = new GridBagConstraints();
/* 188:138 */     gridBagConstraints.gridx = 10;
/* 189:139 */     gridBagConstraints.gridy = 2;
/* 190:140 */     this.jPanel6.add(this.swr10, gridBagConstraints);
/* 191:    */     
/* 192:142 */     this.text10.setBackground(new Color(193, 132, 29));
/* 193:143 */     this.text10.setFont(new Font("Dialog", 0, 8));
/* 194:144 */     this.text10.setForeground(new Color(255, 255, 255));
/* 195:145 */     this.text10.setHorizontalAlignment(0);
/* 196:146 */     this.text10.setText("10");
/* 197:147 */     this.text10.setBorder(new LineBorder(new Color(255, 255, 255)));
/* 198:148 */     this.text10.setMaximumSize(new Dimension(26, 12));
/* 199:149 */     this.text10.setMinimumSize(new Dimension(13, 6));
/* 200:150 */     this.text10.setPreferredSize(new Dimension(26, 12));
/* 201:151 */     this.text10.setOpaque(true);
/* 202:152 */     gridBagConstraints = new GridBagConstraints();
/* 203:153 */     gridBagConstraints.gridx = 10;
/* 204:154 */     gridBagConstraints.gridy = 1;
/* 205:155 */     this.jPanel6.add(this.text10, gridBagConstraints);
/* 206:    */     
/* 207:157 */     this.swr9.setInitial(false);
/* 208:158 */     this.swr9.setMaximumSize(new Dimension(26, 44));
/* 209:159 */     this.swr9.setName("2");
/* 210:160 */     this.swr9.setToggle(true);
/* 211:161 */     this.swr9.setTruepos(false);
/* 212:162 */     gridBagConstraints = new GridBagConstraints();
/* 213:163 */     gridBagConstraints.gridx = 9;
/* 214:164 */     gridBagConstraints.gridy = 2;
/* 215:165 */     this.jPanel6.add(this.swr9, gridBagConstraints);
/* 216:    */     
/* 217:167 */     this.text9.setBackground(new Color(193, 132, 29));
/* 218:168 */     this.text9.setFont(new Font("Dialog", 0, 8));
/* 219:169 */     this.text9.setForeground(new Color(255, 255, 255));
/* 220:170 */     this.text9.setHorizontalAlignment(0);
/* 221:171 */     this.text9.setText("9");
/* 222:172 */     this.text9.setBorder(new LineBorder(new Color(255, 255, 255)));
/* 223:173 */     this.text9.setMaximumSize(new Dimension(26, 12));
/* 224:174 */     this.text9.setMinimumSize(new Dimension(13, 6));
/* 225:175 */     this.text9.setPreferredSize(new Dimension(26, 12));
/* 226:176 */     this.text9.setOpaque(true);
/* 227:177 */     gridBagConstraints = new GridBagConstraints();
/* 228:178 */     gridBagConstraints.gridx = 9;
/* 229:179 */     gridBagConstraints.gridy = 1;
/* 230:180 */     this.jPanel6.add(this.text9, gridBagConstraints);
/* 231:    */     
/* 232:182 */     this.swr11.setInitial(false);
/* 233:183 */     this.swr11.setMaximumSize(new Dimension(26, 44));
/* 234:184 */     this.swr11.setName("0");
/* 235:185 */     this.swr11.setToggle(true);
/* 236:186 */     this.swr11.setTruepos(false);
/* 237:187 */     gridBagConstraints = new GridBagConstraints();
/* 238:188 */     gridBagConstraints.gridx = 11;
/* 239:189 */     gridBagConstraints.gridy = 2;
/* 240:190 */     this.jPanel6.add(this.swr11, gridBagConstraints);
/* 241:    */     
/* 242:192 */     this.text11.setBackground(new Color(193, 132, 29));
/* 243:193 */     this.text11.setFont(new Font("Dialog", 0, 8));
/* 244:194 */     this.text11.setForeground(new Color(255, 255, 255));
/* 245:195 */     this.text11.setHorizontalAlignment(0);
/* 246:196 */     this.text11.setText("11");
/* 247:197 */     this.text11.setBorder(new LineBorder(new Color(255, 255, 255)));
/* 248:198 */     this.text11.setMaximumSize(new Dimension(26, 12));
/* 249:199 */     this.text11.setMinimumSize(new Dimension(13, 6));
/* 250:200 */     this.text11.setPreferredSize(new Dimension(26, 12));
/* 251:201 */     this.text11.setOpaque(true);
/* 252:202 */     gridBagConstraints = new GridBagConstraints();
/* 253:203 */     gridBagConstraints.gridx = 11;
/* 254:204 */     gridBagConstraints.gridy = 1;
/* 255:205 */     this.jPanel6.add(this.text11, gridBagConstraints);
/* 256:    */     
/* 257:207 */     this.text6.setBackground(new Color(193, 77, 28));
/* 258:208 */     this.text6.setFont(new Font("Dialog", 0, 8));
/* 259:209 */     this.text6.setForeground(new Color(255, 255, 255));
/* 260:210 */     this.text6.setHorizontalAlignment(0);
/* 261:211 */     this.text6.setText("6");
/* 262:212 */     this.text6.setBorder(new LineBorder(new Color(255, 255, 255)));
/* 263:213 */     this.text6.setMaximumSize(new Dimension(26, 12));
/* 264:214 */     this.text6.setMinimumSize(new Dimension(13, 6));
/* 265:215 */     this.text6.setPreferredSize(new Dimension(26, 12));
/* 266:216 */     this.text6.setOpaque(true);
/* 267:217 */     gridBagConstraints = new GridBagConstraints();
/* 268:218 */     gridBagConstraints.gridx = 6;
/* 269:219 */     gridBagConstraints.gridy = 1;
/* 270:220 */     this.jPanel6.add(this.text6, gridBagConstraints);
/* 271:    */     
/* 272:222 */     this.text7.setBackground(new Color(193, 77, 28));
/* 273:223 */     this.text7.setFont(new Font("Dialog", 0, 8));
/* 274:224 */     this.text7.setForeground(new Color(255, 255, 255));
/* 275:225 */     this.text7.setHorizontalAlignment(0);
/* 276:226 */     this.text7.setText("7");
/* 277:227 */     this.text7.setBorder(new LineBorder(new Color(255, 255, 255)));
/* 278:228 */     this.text7.setMaximumSize(new Dimension(26, 12));
/* 279:229 */     this.text7.setMinimumSize(new Dimension(13, 6));
/* 280:230 */     this.text7.setPreferredSize(new Dimension(26, 12));
/* 281:231 */     this.text7.setOpaque(true);
/* 282:232 */     gridBagConstraints = new GridBagConstraints();
/* 283:233 */     gridBagConstraints.gridx = 7;
/* 284:234 */     gridBagConstraints.gridy = 1;
/* 285:235 */     this.jPanel6.add(this.text7, gridBagConstraints);
/* 286:    */     
/* 287:237 */     this.text8.setBackground(new Color(193, 77, 28));
/* 288:238 */     this.text8.setFont(new Font("Dialog", 0, 8));
/* 289:239 */     this.text8.setForeground(new Color(255, 255, 255));
/* 290:240 */     this.text8.setHorizontalAlignment(0);
/* 291:241 */     this.text8.setText("8");
/* 292:242 */     this.text8.setBorder(new LineBorder(new Color(255, 255, 255)));
/* 293:243 */     this.text8.setMaximumSize(new Dimension(26, 12));
/* 294:244 */     this.text8.setMinimumSize(new Dimension(13, 6));
/* 295:245 */     this.text8.setPreferredSize(new Dimension(26, 12));
/* 296:246 */     this.text8.setOpaque(true);
/* 297:247 */     gridBagConstraints = new GridBagConstraints();
/* 298:248 */     gridBagConstraints.gridx = 8;
/* 299:249 */     gridBagConstraints.gridy = 1;
/* 300:250 */     this.jPanel6.add(this.text8, gridBagConstraints);
/* 301:    */     
/* 302:252 */     this.swr6.setBackground(new Color(193, 77, 28));
/* 303:253 */     this.swr6.setToolTipText("");
/* 304:254 */     this.swr6.setInitial(false);
/* 305:255 */     this.swr6.setMaximumSize(new Dimension(26, 44));
/* 306:256 */     this.swr6.setName("5");
/* 307:257 */     this.swr6.setToggle(true);
/* 308:258 */     this.swr6.setTruepos(false);
/* 309:259 */     gridBagConstraints = new GridBagConstraints();
/* 310:260 */     gridBagConstraints.gridx = 6;
/* 311:261 */     gridBagConstraints.gridy = 2;
/* 312:262 */     this.jPanel6.add(this.swr6, gridBagConstraints);
/* 313:    */     
/* 314:264 */     this.swr7.setBackground(new Color(193, 77, 28));
/* 315:265 */     this.swr7.setInitial(false);
/* 316:266 */     this.swr7.setMaximumSize(new Dimension(26, 44));
/* 317:267 */     this.swr7.setName("4");
/* 318:268 */     this.swr7.setToggle(true);
/* 319:269 */     this.swr7.setTruepos(false);
/* 320:270 */     gridBagConstraints = new GridBagConstraints();
/* 321:271 */     gridBagConstraints.gridx = 7;
/* 322:272 */     gridBagConstraints.gridy = 2;
/* 323:273 */     this.jPanel6.add(this.swr7, gridBagConstraints);
/* 324:    */     
/* 325:275 */     this.swr8.setBackground(new Color(193, 77, 28));
/* 326:276 */     this.swr8.setInitial(false);
/* 327:277 */     this.swr8.setMaximumSize(new Dimension(26, 44));
/* 328:278 */     this.swr8.setName("3");
/* 329:279 */     this.swr8.setToggle(true);
/* 330:280 */     this.swr8.setTruepos(false);
/* 331:281 */     gridBagConstraints = new GridBagConstraints();
/* 332:282 */     gridBagConstraints.gridx = 8;
/* 333:283 */     gridBagConstraints.gridy = 2;
/* 334:284 */     this.jPanel6.add(this.swr8, gridBagConstraints);
/* 335:    */     
/* 336:286 */     this.text0.setBackground(new Color(193, 77, 28));
/* 337:287 */     this.text0.setFont(new Font("Dialog", 0, 8));
/* 338:288 */     this.text0.setForeground(new Color(255, 255, 255));
/* 339:289 */     this.text0.setHorizontalAlignment(0);
/* 340:290 */     this.text0.setText("0");
/* 341:291 */     this.text0.setBorder(new LineBorder(new Color(255, 255, 255)));
/* 342:292 */     this.text0.setMaximumSize(new Dimension(26, 12));
/* 343:293 */     this.text0.setMinimumSize(new Dimension(13, 6));
/* 344:294 */     this.text0.setPreferredSize(new Dimension(26, 12));
/* 345:295 */     this.text0.setOpaque(true);
/* 346:296 */     gridBagConstraints = new GridBagConstraints();
/* 347:297 */     gridBagConstraints.gridx = 0;
/* 348:298 */     gridBagConstraints.gridy = 1;
/* 349:299 */     this.jPanel6.add(this.text0, gridBagConstraints);
/* 350:    */     
/* 351:301 */     this.text1.setBackground(new Color(193, 77, 28));
/* 352:302 */     this.text1.setFont(new Font("Dialog", 0, 8));
/* 353:303 */     this.text1.setForeground(new Color(255, 255, 255));
/* 354:304 */     this.text1.setHorizontalAlignment(0);
/* 355:305 */     this.text1.setText("1");
/* 356:306 */     this.text1.setBorder(new LineBorder(new Color(255, 255, 255)));
/* 357:307 */     this.text1.setMaximumSize(new Dimension(26, 12));
/* 358:308 */     this.text1.setMinimumSize(new Dimension(13, 6));
/* 359:309 */     this.text1.setPreferredSize(new Dimension(26, 12));
/* 360:310 */     this.text1.setOpaque(true);
/* 361:311 */     gridBagConstraints = new GridBagConstraints();
/* 362:312 */     gridBagConstraints.gridx = 1;
/* 363:313 */     gridBagConstraints.gridy = 1;
/* 364:314 */     this.jPanel6.add(this.text1, gridBagConstraints);
/* 365:    */     
/* 366:316 */     this.text2.setBackground(new Color(193, 77, 28));
/* 367:317 */     this.text2.setFont(new Font("Dialog", 0, 8));
/* 368:318 */     this.text2.setForeground(new Color(255, 255, 255));
/* 369:319 */     this.text2.setHorizontalAlignment(0);
/* 370:320 */     this.text2.setText("2");
/* 371:321 */     this.text2.setBorder(new LineBorder(new Color(255, 255, 255)));
/* 372:322 */     this.text2.setMaximumSize(new Dimension(26, 12));
/* 373:323 */     this.text2.setMinimumSize(new Dimension(13, 6));
/* 374:324 */     this.text2.setPreferredSize(new Dimension(26, 12));
/* 375:325 */     this.text2.setOpaque(true);
/* 376:326 */     gridBagConstraints = new GridBagConstraints();
/* 377:327 */     gridBagConstraints.gridx = 2;
/* 378:328 */     gridBagConstraints.gridy = 1;
/* 379:329 */     this.jPanel6.add(this.text2, gridBagConstraints);
/* 380:    */     
/* 381:331 */     this.swr0.setBackground(new Color(193, 77, 28));
/* 382:332 */     this.swr0.setInitial(false);
/* 383:333 */     this.swr0.setMaximumSize(new Dimension(26, 44));
/* 384:334 */     this.swr0.setName("11");
/* 385:335 */     this.swr0.setToggle(true);
/* 386:336 */     this.swr0.setTruepos(false);
/* 387:337 */     gridBagConstraints = new GridBagConstraints();
/* 388:338 */     gridBagConstraints.gridx = 0;
/* 389:339 */     gridBagConstraints.gridy = 2;
/* 390:340 */     this.jPanel6.add(this.swr0, gridBagConstraints);
/* 391:    */     
/* 392:342 */     this.swr1.setBackground(new Color(193, 77, 28));
/* 393:343 */     this.swr1.setInitial(false);
/* 394:344 */     this.swr1.setMaximumSize(new Dimension(26, 44));
/* 395:345 */     this.swr1.setName("10");
/* 396:346 */     this.swr1.setToggle(true);
/* 397:347 */     this.swr1.setTruepos(false);
/* 398:348 */     gridBagConstraints = new GridBagConstraints();
/* 399:349 */     gridBagConstraints.gridx = 1;
/* 400:350 */     gridBagConstraints.gridy = 2;
/* 401:351 */     this.jPanel6.add(this.swr1, gridBagConstraints);
/* 402:    */     
/* 403:353 */     this.swr2.setBackground(new Color(193, 77, 28));
/* 404:354 */     this.swr2.setInitial(false);
/* 405:355 */     this.swr2.setMaximumSize(new Dimension(26, 44));
/* 406:356 */     this.swr2.setName("9");
/* 407:357 */     this.swr2.setToggle(true);
/* 408:358 */     this.swr2.setTruepos(false);
/* 409:359 */     gridBagConstraints = new GridBagConstraints();
/* 410:360 */     gridBagConstraints.gridx = 2;
/* 411:361 */     gridBagConstraints.gridy = 2;
/* 412:362 */     this.jPanel6.add(this.swr2, gridBagConstraints);
/* 413:    */     
/* 414:364 */     this.text3.setBackground(new Color(193, 132, 29));
/* 415:365 */     this.text3.setFont(new Font("Dialog", 0, 8));
/* 416:366 */     this.text3.setForeground(new Color(255, 255, 255));
/* 417:367 */     this.text3.setHorizontalAlignment(0);
/* 418:368 */     this.text3.setText("3");
/* 419:369 */     this.text3.setBorder(new LineBorder(new Color(255, 255, 255)));
/* 420:370 */     this.text3.setMaximumSize(new Dimension(26, 12));
/* 421:371 */     this.text3.setMinimumSize(new Dimension(13, 6));
/* 422:372 */     this.text3.setPreferredSize(new Dimension(26, 12));
/* 423:373 */     this.text3.setOpaque(true);
/* 424:374 */     gridBagConstraints = new GridBagConstraints();
/* 425:375 */     gridBagConstraints.gridx = 3;
/* 426:376 */     gridBagConstraints.gridy = 1;
/* 427:377 */     this.jPanel6.add(this.text3, gridBagConstraints);
/* 428:    */     
/* 429:379 */     this.text4.setBackground(new Color(193, 132, 29));
/* 430:380 */     this.text4.setFont(new Font("Dialog", 0, 8));
/* 431:381 */     this.text4.setForeground(new Color(255, 255, 255));
/* 432:382 */     this.text4.setHorizontalAlignment(0);
/* 433:383 */     this.text4.setText("4");
/* 434:384 */     this.text4.setBorder(new LineBorder(new Color(255, 255, 255)));
/* 435:385 */     this.text4.setMaximumSize(new Dimension(26, 12));
/* 436:386 */     this.text4.setMinimumSize(new Dimension(13, 6));
/* 437:387 */     this.text4.setPreferredSize(new Dimension(26, 12));
/* 438:388 */     this.text4.setOpaque(true);
/* 439:389 */     gridBagConstraints = new GridBagConstraints();
/* 440:390 */     gridBagConstraints.gridx = 4;
/* 441:391 */     gridBagConstraints.gridy = 1;
/* 442:392 */     this.jPanel6.add(this.text4, gridBagConstraints);
/* 443:    */     
/* 444:394 */     this.text5.setBackground(new Color(193, 132, 29));
/* 445:395 */     this.text5.setFont(new Font("Dialog", 0, 8));
/* 446:396 */     this.text5.setForeground(new Color(255, 255, 255));
/* 447:397 */     this.text5.setHorizontalAlignment(0);
/* 448:398 */     this.text5.setText("5");
/* 449:399 */     this.text5.setBorder(new LineBorder(new Color(255, 255, 255)));
/* 450:400 */     this.text5.setMaximumSize(new Dimension(26, 12));
/* 451:401 */     this.text5.setMinimumSize(new Dimension(13, 6));
/* 452:402 */     this.text5.setPreferredSize(new Dimension(26, 12));
/* 453:403 */     this.text5.setOpaque(true);
/* 454:404 */     gridBagConstraints = new GridBagConstraints();
/* 455:405 */     gridBagConstraints.gridx = 5;
/* 456:406 */     gridBagConstraints.gridy = 1;
/* 457:407 */     this.jPanel6.add(this.text5, gridBagConstraints);
/* 458:    */     
/* 459:409 */     this.swr3.setInitial(false);
/* 460:410 */     this.swr3.setMaximumSize(new Dimension(26, 44));
/* 461:411 */     this.swr3.setName("8");
/* 462:412 */     this.swr3.setToggle(true);
/* 463:413 */     this.swr3.setTruepos(false);
/* 464:414 */     gridBagConstraints = new GridBagConstraints();
/* 465:415 */     gridBagConstraints.gridx = 3;
/* 466:416 */     gridBagConstraints.gridy = 2;
/* 467:417 */     this.jPanel6.add(this.swr3, gridBagConstraints);
/* 468:    */     
/* 469:419 */     this.swr4.setInitial(false);
/* 470:420 */     this.swr4.setMaximumSize(new Dimension(26, 44));
/* 471:421 */     this.swr4.setName("7");
/* 472:422 */     this.swr4.setToggle(true);
/* 473:423 */     this.swr4.setTruepos(false);
/* 474:424 */     gridBagConstraints = new GridBagConstraints();
/* 475:425 */     gridBagConstraints.gridx = 4;
/* 476:426 */     gridBagConstraints.gridy = 2;
/* 477:427 */     this.jPanel6.add(this.swr4, gridBagConstraints);
/* 478:    */     
/* 479:429 */     this.swr5.setInitial(false);
/* 480:430 */     this.swr5.setMaximumSize(new Dimension(26, 44));
/* 481:431 */     this.swr5.setName("6");
/* 482:432 */     this.swr5.setToggle(true);
/* 483:433 */     this.swr5.setTruepos(false);
/* 484:434 */     gridBagConstraints = new GridBagConstraints();
/* 485:435 */     gridBagConstraints.gridx = 5;
/* 486:436 */     gridBagConstraints.gridy = 2;
/* 487:437 */     this.jPanel6.add(this.swr5, gridBagConstraints);
/* 488:    */     
/* 489:439 */     this.jLabel1.setBackground(new Color(193, 78, 28));
/* 490:440 */     this.jLabel1.setFont(new Font("Dialog", 0, 8));
/* 491:441 */     this.jLabel1.setForeground(new Color(255, 255, 255));
/* 492:442 */     this.jLabel1.setHorizontalAlignment(0);
/* 493:443 */     this.jLabel1.setText("SWITCH REGISTER");
/* 494:444 */     this.jLabel1.setMaximumSize(new Dimension(312, 10));
/* 495:445 */     this.jLabel1.setMinimumSize(new Dimension(156, 5));
/* 496:446 */     this.jLabel1.setPreferredSize(new Dimension(312, 10));
/* 497:447 */     this.jLabel1.setOpaque(true);
/* 498:448 */     this.jLabel1.addComponentListener(new ComponentAdapter()
/* 499:    */     {
/* 500:    */       public void componentResized(ComponentEvent evt)
/* 501:    */       {
/* 502:450 */         SwitchReg.this.jLabel1ComponentResized(evt);
/* 503:    */       }
/* 504:452 */     });
/* 505:453 */     this.jLabel1.addMouseListener(new MouseAdapter()
/* 506:    */     {
/* 507:    */       public void mouseClicked(MouseEvent evt)
/* 508:    */       {
/* 509:455 */         SwitchReg.this.jLabel1MouseClicked(evt);
/* 510:    */       }
/* 511:458 */     });
/* 512:459 */     gridBagConstraints = new GridBagConstraints();
/* 513:460 */     gridBagConstraints.gridwidth = 12;
/* 514:461 */     this.jPanel6.add(this.jLabel1, gridBagConstraints);
/* 515:    */     
/* 516:463 */     add(this.jPanel6, "Center");
/* 517:    */   }
/* 518:    */   
/* 519:    */   private void jLabel1MouseClicked(MouseEvent evt)
/* 520:    */   {
/* 521:469 */     int bitcount = 0;
/* 522:470 */     int i = 0;
/* 523:471 */     for (i = 0; i < 12; i++) {
/* 524:472 */       if ((1 << i & this.swreg) != 0) {
/* 525:472 */         bitcount++;
/* 526:    */       }
/* 527:    */     }
/* 528:474 */     if ((((bitcount > 6 ? 1 : 0) | (bitcount == 0 ? 1 : 0)) & (bitcount != 12 ? 1 : 0)) != 0)
/* 529:    */     {
/* 530:475 */       this.swr0.setPos(true);this.swr1.setPos(true);this.swr2.setPos(true);this.swr3.setPos(true);this.swr4.setPos(true);this.swr5.setPos(true);
/* 531:476 */       this.swr6.setPos(true);this.swr7.setPos(true);this.swr8.setPos(true);this.swr9.setPos(true);this.swr10.setPos(true);this.swr11.setPos(true);
/* 532:    */     }
/* 533:    */     else
/* 534:    */     {
/* 535:478 */       this.swr0.setPos(false);this.swr1.setPos(false);this.swr2.setPos(false);this.swr3.setPos(false);this.swr4.setPos(false);this.swr5.setPos(false);
/* 536:479 */       this.swr6.setPos(false);this.swr7.setPos(false);this.swr8.setPos(false);this.swr9.setPos(false);this.swr10.setPos(false);this.swr11.setPos(false);
/* 537:    */     }
/* 538:    */   }
/* 539:    */   
/* 540:    */   private void jLabel1ComponentResized(ComponentEvent evt)
/* 541:    */   {
/* 542:485 */     if (this.jLabel1.getWidth() < 300)
/* 543:    */     {
/* 544:486 */       this.jLabel1.setFont(new Font("Dialog", 0, 4));
/* 545:487 */       this.text0.setFont(new Font("Dialog", 0, 4));
/* 546:488 */       this.text1.setFont(new Font("Dialog", 0, 4));
/* 547:489 */       this.text2.setFont(new Font("Dialog", 0, 4));
/* 548:490 */       this.text3.setFont(new Font("Dialog", 0, 4));
/* 549:491 */       this.text4.setFont(new Font("Dialog", 0, 4));
/* 550:492 */       this.text5.setFont(new Font("Dialog", 0, 4));
/* 551:493 */       this.text6.setFont(new Font("Dialog", 0, 4));
/* 552:494 */       this.text7.setFont(new Font("Dialog", 0, 4));
/* 553:495 */       this.text8.setFont(new Font("Dialog", 0, 4));
/* 554:496 */       this.text9.setFont(new Font("Dialog", 0, 4));
/* 555:497 */       this.text10.setFont(new Font("Dialog", 0, 4));
/* 556:498 */       this.text11.setFont(new Font("Dialog", 0, 4));
/* 557:    */     }
/* 558:    */     else
/* 559:    */     {
/* 560:500 */       this.jLabel1.setFont(new Font("Dialog", 0, 8));
/* 561:501 */       this.text0.setFont(new Font("Dialog", 0, 8));
/* 562:502 */       this.text1.setFont(new Font("Dialog", 0, 8));
/* 563:503 */       this.text2.setFont(new Font("Dialog", 0, 8));
/* 564:504 */       this.text3.setFont(new Font("Dialog", 0, 8));
/* 565:505 */       this.text4.setFont(new Font("Dialog", 0, 8));
/* 566:506 */       this.text5.setFont(new Font("Dialog", 0, 8));
/* 567:507 */       this.text6.setFont(new Font("Dialog", 0, 8));
/* 568:508 */       this.text7.setFont(new Font("Dialog", 0, 8));
/* 569:509 */       this.text8.setFont(new Font("Dialog", 0, 8));
/* 570:510 */       this.text9.setFont(new Font("Dialog", 0, 8));
/* 571:511 */       this.text10.setFont(new Font("Dialog", 0, 8));
/* 572:512 */       this.text11.setFont(new Font("Dialog", 0, 8));
/* 573:    */     }
/* 574:    */   }
/* 575:    */   
/* 576:    */   public void addPropertyChangeListener(PropertyChangeListener pl)
/* 577:    */   {
/* 578:517 */     this.swrlisteners.addPropertyChangeListener(pl);
/* 579:    */   }
/* 580:    */   
/* 581:    */   public void removePropertyChangeListener(PropertyChangeListener pl)
/* 582:    */   {
/* 583:522 */     this.swrlisteners.removePropertyChangeListener(pl);
/* 584:    */   }
/* 585:    */   
/* 586:    */   private void swrPropertyChange(PropertyChangeEvent evt)
/* 587:    */   {
/* 588:526 */     Boolean objswr = (Boolean)evt.getNewValue();
/* 589:527 */     boolean valswr = objswr.booleanValue();
/* 590:528 */     int mask = Integer.parseInt(evt.getPropertyName());
/* 591:529 */     int oldreg = this.swreg;
/* 592:530 */     if (valswr) {
/* 593:531 */       this.swreg |= 1 << mask;
/* 594:    */     } else {
/* 595:533 */       this.swreg &= (1 << mask ^ 0xFFFFFFFF);
/* 596:    */     }
/* 597:536 */     this.swrlisteners.firePropertyChange(getName(), oldreg, this.swreg);
/* 598:    */   }
/* 599:    */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Panel.SwitchReg
 * JD-Core Version:    0.7.0.1
 */