/*   1:    */ package Devices;
/*   2:    */ 
/*   3:    */ import Logic.PC8E;
/*   4:    */ import java.awt.BasicStroke;
/*   5:    */ import java.awt.Color;
/*   6:    */ import java.awt.Dimension;
/*   7:    */ import java.awt.Graphics;
/*   8:    */ import java.awt.Graphics2D;
/*   9:    */ import java.awt.RenderingHints;
/*  10:    */ import java.awt.Stroke;
/*  11:    */ import java.awt.geom.AffineTransform;
/*  12:    */ import java.io.PrintStream;
/*  13:    */ import javax.swing.JComponent;
/*  14:    */ import javax.swing.JLabel;
/*  15:    */ 
/*  16:    */ public class PTape
/*  17:    */   extends JComponent
/*  18:    */ {
/*  19:    */   private PtrPtp pt;
/*  20:    */   private Turn doit;
/*  21: 23 */   private int unit = 0;
/*  22: 24 */   private boolean right = false;
/*  23: 25 */   private boolean first = false;
/*  24:    */   private int width;
/*  25:    */   private int height;
/*  26:    */   private int twidth;
/*  27:    */   private int diam;
/*  28:    */   private Color back;
/*  29:    */   private Color dark;
/*  30:    */   private int local_line;
/*  31:    */   private int tape_line;
/*  32:    */   private int layers;
/*  33:    */   private double start;
/*  34:    */   private double pos;
/*  35:    */   private int hole;
/*  36: 38 */   private int[] holes = new int[80];
/*  37:    */   private Thread update;
/*  38:    */   private static final float PI = 3.141593F;
/*  39:    */   private static final float MULTIP = 0.01745329F;
/*  40: 44 */   private static final Dimension MIN_SIZE = new Dimension(74, 76);
/*  41: 45 */   private static final Dimension PREF_SIZE = new Dimension(148, 148);
/*  42: 48 */   private static final RenderingHints AALIAS = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*  43: 51 */   Stroke defaultStroke = new BasicStroke(1.0F);
/*  44: 52 */   Stroke tapeStroke = new BasicStroke(1.0F);
/*  45:    */   
/*  46:    */   public PTape()
/*  47:    */   {
/*  48: 57 */     setPreferredSize(PREF_SIZE);
/*  49: 58 */     setMinimumSize(MIN_SIZE);
/*  50: 59 */     setBackground(new Color(100, 100, 100));
/*  51: 60 */     for (int i = 0; i < 80; i++) {
/*  52: 60 */       this.holes[i] = 0;
/*  53:    */     }
/*  54: 61 */     this.doit = new Turn();
/*  55:    */   }
/*  56:    */   
/*  57:    */   public void startPT(PtrPtp pt, String name)
/*  58:    */   {
/*  59: 66 */     this.pt = pt;
/*  60: 67 */     this.first = true;
/*  61: 68 */     this.update = new Thread(this.doit, name);
/*  62: 69 */     this.update.start();
/*  63:    */   }
/*  64:    */   
/*  65:    */   public int getUnit()
/*  66:    */   {
/*  67: 73 */     return this.unit;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public void setUnit(int unit)
/*  71:    */   {
/*  72: 77 */     this.unit = unit;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public boolean getRight()
/*  76:    */   {
/*  77: 81 */     return this.right;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public void setRight(boolean right)
/*  81:    */   {
/*  82: 85 */     this.right = right;
/*  83: 86 */     repaint();
/*  84:    */   }
/*  85:    */   
/*  86:    */   public Color getBackground()
/*  87:    */   {
/*  88: 91 */     return this.back;
/*  89:    */   }
/*  90:    */   
/*  91:    */   public void setBackground(Color back)
/*  92:    */   {
/*  93: 96 */     this.back = back;
/*  94: 97 */     this.dark = back.darker().darker();
/*  95:    */   }
/*  96:    */   
/*  97:    */   public void paint(Graphics g1d)
/*  98:    */   {
/*  99:104 */     this.width = getWidth();
/* 100:105 */     this.height = getHeight();
/* 101:107 */     if (this.unit == 0)
/* 102:    */     {
/* 103:108 */       if (!this.right) {
/* 104:108 */         this.tape_line = (this.local_line - 100);
/* 105:108 */       } else if (this.first) {
/* 106:108 */         this.tape_line = (this.pt.tapesize[this.unit] - this.local_line);
/* 107:    */       }
/* 108:110 */       Graphics2D g = (Graphics2D)g1d;
/* 109:111 */       AffineTransform a = AffineTransform.getScaleInstance(-1.0D, 1.0D);
/* 110:112 */       a.translate(-this.width, 0.0D);
/* 111:113 */       if (this.right) {
/* 112:113 */         g.transform(a);
/* 113:    */       }
/* 114:114 */       g.setBackground(this.back);
/* 115:115 */       g.addRenderingHints(AALIAS);
/* 116:116 */       g.setStroke(this.defaultStroke);
/* 117:117 */       g.setColor(this.back);
/* 118:118 */       g.fillRect(0, 0, this.width, this.height);
/* 119:119 */       paintBorder(g);
/* 120:120 */       if ((this.first == true) && (this.pt.pc8e.tape[this.unit] != null))
/* 121:    */       {
/* 122:121 */         this.pt.pDiodes.setBackground(Color.white);
/* 123:122 */         g.setStroke(this.tapeStroke);
/* 124:123 */         this.layers = (this.tape_line / 80);
/* 125:124 */         if (this.layers % 2 == 0)
/* 126:    */         {
/* 127:125 */           this.start = -110.0D;
/* 128:126 */           this.pos = (-this.tape_line % 80);
/* 129:    */         }
/* 130:    */         else
/* 131:    */         {
/* 132:128 */           this.start = -190.0D;
/* 133:129 */           this.pos = (this.tape_line % 80);
/* 134:    */         }
/* 135:131 */         for (int i = 0; i <= this.layers; i++)
/* 136:    */         {
/* 137:132 */           this.diam = (this.width * 2 - i);
/* 138:133 */           if (i % 2 == 0) {
/* 139:133 */             g.setColor(Color.white);
/* 140:    */           } else {
/* 141:133 */             g.setColor(this.back);
/* 142:    */           }
/* 143:134 */           if (i == this.layers) {
/* 144:134 */             g.drawArc(this.width - this.diam / 2, this.height / 4 - this.diam / 2, this.diam, this.diam, (int)this.start, (int)this.pos);
/* 145:    */           } else {
/* 146:135 */             g.drawArc(this.width - this.diam / 2, this.height / 4 - this.diam / 2, this.diam, this.diam, -110, -80);
/* 147:    */           }
/* 148:    */         }
/* 149:137 */         double posa = (this.start + this.pos) * 0.0174532923847437D;
/* 150:138 */         int plusx = (int)(Math.cos(posa) * this.diam / 2.0D);
/* 151:139 */         int plusy = (int)(Math.sin(posa) * this.diam / 2.0D);
/* 152:140 */         g.setColor(Color.white);
/* 153:141 */         g.drawLine(this.width, this.height / 4, this.width + plusx, this.height / 4 - plusy);
/* 154:    */       }
/* 155:142 */       else if (this.first == true)
/* 156:    */       {
/* 157:143 */         this.pt.pDiodes.setBackground(this.back);
/* 158:    */       }
/* 159:    */     }
/* 160:146 */     if (this.unit == 1)
/* 161:    */     {
/* 162:147 */       this.twidth = (this.width * 4 / 5);
/* 163:148 */       this.tape_line = this.local_line;
/* 164:149 */       Graphics2D g = (Graphics2D)g1d;
/* 165:150 */       g.setStroke(this.defaultStroke);
/* 166:151 */       g.setColor(this.back);
/* 167:152 */       g.fillRect(0, 0, this.width, this.height);
/* 168:153 */       if ((this.first == true) && (this.pt.pc8e.tape[this.unit] != null))
/* 169:    */       {
/* 170:154 */         this.layers = (this.tape_line / 80);
/* 171:155 */         this.hole = (this.tape_line % 80);
/* 172:156 */         for (int i = 0; i < this.layers; i++)
/* 173:    */         {
/* 174:157 */           this.diam = (this.width * 2 - i);
/* 175:158 */           if (i % 2 == 0) {
/* 176:158 */             g.setColor(Color.white);
/* 177:    */           } else {
/* 178:158 */             g.setColor(this.back);
/* 179:    */           }
/* 180:159 */           g.drawLine(this.width / 10, 160 + i, this.width / 10 + this.twidth, 160 + i);
/* 181:    */         }
/* 182:161 */         if (this.layers % 2 == 0) {
/* 183:161 */           g.setColor(Color.white);
/* 184:    */         } else {
/* 185:161 */           g.setColor(this.dark);
/* 186:    */         }
/* 187:162 */         g.fillRect(this.width / 10, 0, this.twidth, this.hole * 2);
/* 188:163 */         if (this.layers % 2 == 1) {
/* 189:163 */           g.setColor(Color.white);
/* 190:    */         } else {
/* 191:163 */           g.setColor(this.dark);
/* 192:    */         }
/* 193:164 */         for (int i = 0; i < this.hole; i++) {
/* 194:164 */           Holes(g, i);
/* 195:    */         }
/* 196:165 */         g.fillRect(this.width / 10, this.hole * 2, this.twidth, 160 - this.hole * 2);
/* 197:166 */         if (this.layers % 2 == 0) {
/* 198:166 */           g.setColor(Color.white);
/* 199:    */         } else {
/* 200:166 */           g.setColor(this.dark);
/* 201:    */         }
/* 202:167 */         if (this.layers > 0) {
/* 203:167 */           for (int i = this.hole; i < 80; i++) {
/* 204:167 */             Holes(g, i);
/* 205:    */           }
/* 206:    */         }
/* 207:    */       }
/* 208:    */     }
/* 209:    */   }
/* 210:    */   
/* 211:    */   private void Holes(Graphics2D g, int i)
/* 212:    */   {
/* 213:173 */     g.fillOval(this.width / 10 + this.twidth * 6 / 10, i * 2, 1, 1);
/* 214:174 */     if ((this.holes[i] >> 0 & 0x1) != 0) {
/* 215:174 */       g.fillOval(this.width / 10 + this.twidth * 9 / 10, i * 2, 2, 2);
/* 216:    */     }
/* 217:175 */     if ((this.holes[i] >> 1 & 0x1) != 0) {
/* 218:175 */       g.fillOval(this.width / 10 + this.twidth * 8 / 10, i * 2, 2, 2);
/* 219:    */     }
/* 220:176 */     if ((this.holes[i] >> 2 & 0x1) != 0) {
/* 221:176 */       g.fillOval(this.width / 10 + this.twidth * 7 / 10, i * 2, 2, 2);
/* 222:    */     }
/* 223:177 */     if ((this.holes[i] >> 3 & 0x1) != 0) {
/* 224:177 */       g.fillOval(this.width / 10 + this.twidth * 5 / 10, i * 2, 2, 2);
/* 225:    */     }
/* 226:178 */     if ((this.holes[i] >> 4 & 0x1) != 0) {
/* 227:178 */       g.fillOval(this.width / 10 + this.twidth * 4 / 10, i * 2, 2, 2);
/* 228:    */     }
/* 229:179 */     if ((this.holes[i] >> 5 & 0x1) != 0) {
/* 230:179 */       g.fillOval(this.width / 10 + this.twidth * 3 / 10, i * 2, 2, 2);
/* 231:    */     }
/* 232:180 */     if ((this.holes[i] >> 6 & 0x1) != 0) {
/* 233:180 */       g.fillOval(this.width / 10 + this.twidth * 2 / 10, i * 2, 2, 2);
/* 234:    */     }
/* 235:181 */     if ((this.holes[i] >> 7 & 0x1) != 0) {
/* 236:181 */       g.fillOval(this.width / 10 + this.twidth * 1 / 10, i * 2, 2, 2);
/* 237:    */     }
/* 238:    */   }
/* 239:    */   
/* 240:    */   public void setHole(int data)
/* 241:    */   {
/* 242:185 */     if (this.unit == 1) {
/* 243:186 */       this.holes[(this.pt.pc8e.line[this.unit] % 80)] = data;
/* 244:    */     }
/* 245:    */   }
/* 246:    */   
/* 247:    */   public class Turn
/* 248:    */     implements Runnable
/* 249:    */   {
/* 250:192 */     int sleep = 20;
/* 251:    */     int inc;
/* 252:    */     
/* 253:    */     public void run()
/* 254:    */     {
/* 255:    */       for (;;)
/* 256:    */       {
/* 257:    */         try
/* 258:    */         {
/* 259:197 */           if ((PTape.this.pt.local[PTape.this.unit] & (PTape.this.pt.pc8e.tape[PTape.this.unit] != null ? 1 : 0)) != 0)
/* 260:    */           {
/* 261:198 */             if (PTape.this.unit == 0) {
/* 262:198 */               this.inc = 300;
/* 263:    */             } else {
/* 264:198 */               this.inc = 50;
/* 265:    */             }
/* 266:199 */             if (PTape.this.right)
/* 267:    */             {
/* 268:200 */               if (((PTape.this.pt.direction[PTape.this.unit] == 1 ? 1 : 0) & (PTape.this.pt.pc8e.line[PTape.this.unit] < PTape.this.pt.tapesize[PTape.this.unit] - this.inc ? 1 : 0)) != 0)
/* 269:    */               {
/* 270:200 */                 int tmp190_187 = PTape.this.unit; int[] tmp190_180 = PTape.this.pt.pc8e.line;tmp190_180[tmp190_187] = ((int)(tmp190_180[tmp190_187] + this.inc * this.sleep / 1000.0D));
/* 271:    */               }
/* 272:201 */               if (((PTape.this.pt.direction[PTape.this.unit] == -1 ? 1 : 0) & (PTape.this.pt.pc8e.line[PTape.this.unit] > this.inc ? 1 : 0)) != 0)
/* 273:    */               {
/* 274:201 */                 int tmp294_291 = PTape.this.unit; int[] tmp294_284 = PTape.this.pt.pc8e.line;tmp294_284[tmp294_291] = ((int)(tmp294_284[tmp294_291] - this.inc * this.sleep / 1000.0D));
/* 275:    */               }
/* 276:    */             }
/* 277:    */           }
/* 278:204 */           if (PTape.this.local_line != PTape.this.pt.pc8e.line[PTape.this.unit])
/* 279:    */           {
/* 280:205 */             PTape.this.local_line = PTape.this.pt.pc8e.line[PTape.this.unit];
/* 281:    */             
/* 282:207 */             PTape.this.repaint();
/* 283:208 */             this.sleep = 20;
/* 284:    */           }
/* 285:210 */           Thread.sleep(this.sleep);
/* 286:211 */           this.sleep += 1;
/* 287:    */           
/* 288:213 */           continue;System.out.println("Feed error" + e);
/* 289:    */         }
/* 290:    */         catch (InterruptedException e) {}
/* 291:    */       }
/* 292:    */     }
/* 293:    */     
/* 294:    */     public Turn() {}
/* 295:    */   }
/* 296:    */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Devices.PTape
 * JD-Core Version:    0.7.0.1
 */