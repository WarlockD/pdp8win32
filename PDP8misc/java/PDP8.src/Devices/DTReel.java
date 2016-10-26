/*   1:    */ 
/*   2:    */ 
/*   3:    */ Logic.TD8E
/*   4:    */ java.awt.BasicStroke
/*   5:    */ java.awt.Color
/*   6:    */ java.awt.Dimension
/*   7:    */ java.awt.Graphics
/*   8:    */ java.awt.Graphics2D
/*   9:    */ java.awt.Polygon
/*  10:    */ java.awt.RenderingHints
/*  11:    */ java.awt.Stroke
/*  12:    */ java.awt.geom.AffineTransform
/*  13:    */ java.io.PrintStream
/*  14:    */ javax.swing.JComponent
/*  15:    */ 
/*  16:    */ DTReel
/*  17:    */   
/*  18:    */ 
/*  19:    */   dt
/*  20:    */   doit
/*  21:    */   update
/*  22: 24 */   unit = 0
/*  23:    */   size
/*  24: 26 */   right = 
/*  25: 27 */   oldwidth = 0
/*  26: 28 */   oldheight = 0
/*  27: 29 */   first = 
/*  28: 31 */   start_ang = 0.0D
/*  29:    */   reel_x
/*  30:    */   reel_y
/*  31:    */   plate_top_y
/*  32:    */   guide_bottom_y
/*  33:    */   guide_left_x
/*  34:    */   guide_right_x
/*  35:    */   hub_radius
/*  36:    */   reel_radius
/*  37:    */   head_left_x
/*  38:    */   head_top_y
/*  39:    */   head_top_y1
/*  40:    */   head_bottom_y
/*  41:    */   angle_1
/*  42:    */   angle_2
/*  43:    */   angle
/*  44:    */   dist_guide_reel
/*  45:    */   back
/*  46:    */   dark
/*  47:    */   local_line
/*  48:    */   reel_line
/*  49:    */   tape_radius
/*  50:    */   PI = 3.141593F
/*  51:    */   MULTIP = 0.01745329F
/*  52: 56 */   MIN_SIZE = 68, 104
/*  53: 57 */   PREF_SIZE = 140, 224
/*  54:    */   REEL_CENTER_X = 0.5F
/*  55:    */   REEL_CENTER_Y = 0.65F
/*  56:    */   PLATE_TOP_Y = 0.03F
/*  57:    */   GUIDE_BOTTOM_Y = 0.34F
/*  58:    */   GUIDE_LEFT_X = 0.3F
/*  59:    */   GUIDE_RIGHT_X = 0.83F
/*  60:    */   HUB_RADIUS = 0.27F
/*  61:    */   REEL_RADIUS = 0.4F
/*  62:    */   HEAD_LEFT_X = 0.88F
/*  63:    */   HEAD_TOP_Y = 0.01F
/*  64:    */   HEAD_TOP_Y1 = 0.05F
/*  65:    */   HEAD_BOTTOM_Y = 0.3F
/*  66:    */   MIL = 0.0015D
/*  67: 73 */   AALIAS = KEY_ANTIALIASING, VALUE_ANTIALIAS_ON
/*  68: 76 */   defaultStroke = 1.0F
/*  69: 77 */   hubStroke = 2.0F
/*  70:    */   
/*  71:    */   DTReel
/*  72:    */   
/*  73: 82 */     setPreferredSizePREF_SIZE
/*  74: 83 */     setMinimumSizeMIN_SIZE
/*  75: 84 */     setBackground100, 100, 100
/*  76: 85 */     doit = new Turn();
/*  77:    */   }
/*  78:    */   
/*  79:    */   public void startDt(Dectape dt, String name)
/*  80:    */   {
/*  81: 90 */     this.dt = dt;
/*  82: 91 */     this.first = true;
/*  83: 92 */     this.update = new Thread(this.doit, name);
/*  84: 93 */     this.update.start();
/*  85:    */   }
/*  86:    */   
/*  87:    */   public int getUnit()
/*  88:    */   {
/*  89: 97 */     return this.unit;
/*  90:    */   }
/*  91:    */   
/*  92:    */   public void setUnit(int unit)
/*  93:    */   {
/*  94:101 */     this.unit = unit;
/*  95:    */   }
/*  96:    */   
/*  97:    */   public double getPos()
/*  98:    */   {
/*  99:105 */     return this.start_ang;
/* 100:    */   }
/* 101:    */   
/* 102:    */   public void setPos(double angle)
/* 103:    */   {
/* 104:109 */     this.start_ang = angle;
/* 105:110 */     repaint();
/* 106:    */   }
/* 107:    */   
/* 108:    */   public boolean getRight()
/* 109:    */   {
/* 110:114 */     return this.right;
/* 111:    */   }
/* 112:    */   
/* 113:    */   public void setRight(boolean right)
/* 114:    */   {
/* 115:118 */     this.right = right;
/* 116:119 */     repaint();
/* 117:    */   }
/* 118:    */   
/* 119:    */   public Color getBackground()
/* 120:    */   {
/* 121:124 */     return this.back;
/* 122:    */   }
/* 123:    */   
/* 124:    */   public void setBackground(Color back)
/* 125:    */   {
/* 126:129 */     this.back = back;
/* 127:130 */     this.dark = back.darker().darker();
/* 128:    */   }
/* 129:    */   
/* 130:    */   public void paint(Graphics g1d)
/* 131:    */   {
/* 132:136 */     int width = getWidth();
/* 133:137 */     int height = getHeight();
/* 134:138 */     Polygon head = new Polygon();
/* 135:139 */     if (((width != this.oldwidth ? 1 : 0) | (height != this.oldheight ? 1 : 0)) != 0)
/* 136:    */     {
/* 137:140 */       this.oldwidth = width;
/* 138:141 */       this.oldheight = height;
/* 139:142 */       this.reel_x = ((int)(width * 0.5F));
/* 140:143 */       this.reel_y = ((int)(height * 0.65F));
/* 141:144 */       if (width - this.reel_x > height - this.reel_y)
/* 142:    */       {
/* 143:145 */         this.reel_radius = ((int)((height - this.reel_y) * 0.4F * 2.0F));
/* 144:146 */         this.hub_radius = ((int)((height - this.reel_y) * 0.27F * 2.0F));
/* 145:    */       }
/* 146:    */       else
/* 147:    */       {
/* 148:148 */         this.reel_radius = ((int)((width - this.reel_x) * 0.4F * 2.0F));
/* 149:149 */         this.hub_radius = ((int)((width - this.reel_x) * 0.27F * 2.0F));
/* 150:    */       }
/* 151:151 */       this.plate_top_y = ((int)(height * 0.03F));
/* 152:152 */       this.guide_bottom_y = ((int)(height * 0.34F));
/* 153:    */       
/* 154:154 */       this.guide_right_x = ((int)(width * 0.83F));
/* 155:155 */       this.guide_left_x = ((int)(width * 0.83F - height * 0.34F));
/* 156:156 */       this.head_left_x = ((int)(width * 0.88F));
/* 157:157 */       this.head_top_y = ((int)(height * 0.01F));
/* 158:158 */       this.head_top_y1 = ((int)(height * 0.05F));
/* 159:159 */       this.head_bottom_y = ((int)(height * 0.3F));
/* 160:160 */       this.angle_1 = Math.atan((this.guide_right_x - this.reel_x) / (this.reel_y - this.guide_bottom_y));
/* 161:161 */       this.dist_guide_reel = Math.sqrt((this.guide_right_x - this.reel_x) * (this.guide_right_x - this.reel_x) + (this.reel_y - this.guide_bottom_y) * (this.reel_y - this.guide_bottom_y));
/* 162:    */     }
/* 163:164 */     if (this.right) {
/* 164:164 */       this.reel_line = this.local_line;
/* 165:164 */     } else if (this.first) {
/* 166:164 */       this.reel_line = (this.dt.tapesize[this.unit] - this.local_line);
/* 167:    */     }
/* 168:165 */     double windings = (Math.sqrt(1.0D + 0.0015D * this.reel_line / (471.23892F * this.hub_radius * 4.5D / width)) - 1.0D) / 0.003D;
/* 169:166 */     this.tape_radius = ((int)((windings * 0.0015D + 1.0D) * this.hub_radius));
/* 170:167 */     this.start_ang = (windings / 2.0D);
/* 171:    */     
/* 172:169 */     Graphics2D g = (Graphics2D)g1d;
/* 173:170 */     AffineTransform a = AffineTransform.getScaleInstance(-1.0D, 1.0D);
/* 174:171 */     a.translate(-width, 0.0D);
/* 175:172 */     if (this.right) {
/* 176:172 */       g.transform(a);
/* 177:    */     }
/* 178:173 */     g.setBackground(this.back);
/* 179:174 */     g.addRenderingHints(AALIAS);
/* 180:175 */     g.setStroke(this.defaultStroke);
/* 181:    */     
/* 182:177 */     g.setColor(this.back);
/* 183:178 */     g.fillRect(0, 0, width, height);
/* 184:179 */     paintBorder(g);
/* 185:180 */     g.setColor(Color.gray);
/* 186:181 */     g.fill3DRect(this.guide_left_x, this.plate_top_y, this.guide_right_x - this.guide_left_x, this.guide_bottom_y - this.plate_top_y, true);
/* 187:182 */     g.setColor(Color.lightGray);
/* 188:183 */     head.addPoint(this.head_left_x, this.head_top_y1);
/* 189:184 */     head.addPoint(width, this.head_top_y);
/* 190:185 */     head.addPoint(width, this.head_bottom_y);
/* 191:186 */     head.addPoint(this.head_left_x, this.head_bottom_y);
/* 192:187 */     head.addPoint(this.head_left_x, this.head_top_y1);
/* 193:188 */     g.fillPolygon(head);
/* 194:189 */     if ((this.first == true) && (this.dt.td8e.tape[this.unit] != null))
/* 195:    */     {
/* 196:190 */       g.setColor(Color.red);
/* 197:191 */       g.drawLine(this.guide_right_x, this.head_top_y, width, this.head_top_y);
/* 198:192 */       g.setColor(Color.red);
/* 199:193 */       this.angle_2 = Math.asin((this.tape_radius - (this.guide_right_x - this.guide_left_x)) / this.dist_guide_reel);
/* 200:194 */       this.angle = (this.angle_1 + this.angle_2);
/* 201:195 */       int gtx = (int)((this.guide_right_x - this.guide_left_x) * Math.cos(this.angle) * 0.98D);
/* 202:196 */       int gty = (int)((this.guide_right_x - this.guide_left_x) * Math.sin(this.angle) * 0.98D);
/* 203:197 */       double ll = this.dist_guide_reel * Math.cos(this.angle_2);
/* 204:198 */       int ex = (int)(-ll * Math.sin(this.angle));
/* 205:199 */       int ey = (int)(ll * Math.cos(this.angle));
/* 206:200 */       g.drawLine(this.guide_right_x - gtx, this.guide_bottom_y - gty, this.guide_right_x - gtx + ex, this.guide_bottom_y - gty + ey);
/* 207:    */     }
/* 208:202 */     g.setColor(Color.darkGray);
/* 209:203 */     g.fillArc(this.guide_left_x, 0, 2 * (this.guide_right_x - this.guide_left_x), 2 * (this.guide_right_x - this.guide_left_x), 90, 90);
/* 210:204 */     g.setColor(Color.white);
/* 211:205 */     g.drawArc(this.guide_left_x, 0, 2 * (this.guide_right_x - this.guide_left_x), 2 * (this.guide_right_x - this.guide_left_x), 90, 90);
/* 212:206 */     if ((this.first == true) && (this.dt.td8e.tape[this.unit] != null))
/* 213:    */     {
/* 214:207 */       g.setColor(Color.red);
/* 215:208 */       g.fillOval(this.reel_x - this.tape_radius, this.reel_y - this.tape_radius, this.tape_radius * 2, this.tape_radius * 2);
/* 216:209 */       g.setColor(new Color(255, 255, 255, 200));
/* 217:210 */       g.fillOval(this.reel_x - this.reel_radius, this.reel_y - this.reel_radius, this.reel_radius * 2, this.reel_radius * 2);
/* 218:    */     }
/* 219:212 */     g.setColor(Color.black);
/* 220:213 */     g.fillOval(this.reel_x - this.hub_radius, this.reel_y - this.hub_radius, this.hub_radius * 2, this.hub_radius * 2);
/* 221:    */     
/* 222:215 */     g.setColor(new Color(50, 50, 80));
/* 223:    */     
/* 224:217 */     g.setStroke(this.hubStroke);
/* 225:218 */     for (int spoke = 0; spoke < 6; spoke++) {
/* 226:219 */       Spoke(g, this.start_ang + spoke * 3.141593F / 3.0F);
/* 227:    */     }
/* 228:    */   }
/* 229:    */   
/* 230:    */   private void Spoke(Graphics2D g, double angle)
/* 231:    */   {
/* 232:225 */     Polygon p = new Polygon();
/* 233:226 */     double x = Math.cos(angle);
/* 234:227 */     double y = Math.sin(angle);
/* 235:228 */     double x1 = Math.cos(angle - 0.4363323152065277D);
/* 236:229 */     double y1 = Math.sin(angle - 0.4363323152065277D);
/* 237:230 */     double x2 = Math.cos(angle + 0.4363323152065277D);
/* 238:231 */     double y2 = Math.sin(angle + 0.4363323152065277D);
/* 239:232 */     p.addPoint(this.reel_x - (int)(x * this.hub_radius * 0.1D), this.reel_y + (int)(y * this.hub_radius * 0.1D));
/* 240:233 */     p.addPoint(this.reel_x + (int)(x1 * this.hub_radius * 0.5D), this.reel_y - (int)(y1 * this.hub_radius * 0.5D));
/* 241:234 */     p.addPoint(this.reel_x + (int)(x * this.hub_radius), this.reel_y - (int)(y * this.hub_radius));
/* 242:235 */     p.addPoint(this.reel_x + (int)(x2 * this.hub_radius * 0.5D), this.reel_y - (int)(y2 * this.hub_radius * 0.5D));
/* 243:236 */     p.addPoint(this.reel_x - (int)(x * this.hub_radius * 0.1D), this.reel_y + (int)(y * this.hub_radius * 0.1D));
/* 244:237 */     g.fillPolygon(p);
/* 245:    */   }
/* 246:    */   
/* 247:    */   public class Turn
/* 248:    */     implements Runnable
/* 249:    */   {
/* 250:242 */     int sleep = 20;
/* 251:    */     
/* 252:    */     public void run()
/* 253:    */     {
/* 254:    */       for (;;)
/* 255:    */       {
/* 256:    */         try
/* 257:    */         {
/* 258:246 */           if (((DTReel.this.dt.local[DTReel.this.unit] & (DTReel.this.dt.td8e.tape[DTReel.this.unit] != null ? 1 : 0)) != 0) && 
/* 259:247 */             (DTReel.this.right))
/* 260:    */           {
/* 261:248 */             if (((DTReel.this.dt.direction[DTReel.this.unit] == 1 ? 1 : 0) & (DTReel.this.dt.td8e.line[DTReel.this.unit] < DTReel.this.dt.tapesize[DTReel.this.unit] - 33300 ? 1 : 0)) != 0)
/* 262:    */             {
/* 263:248 */               int tmp162_159 = DTReel.this.unit; int[] tmp162_152 = DTReel.this.dt.td8e.line;tmp162_152[tmp162_159] = ((int)(tmp162_152[tmp162_159] + 33300 * this.sleep / 1000.0D));
/* 264:    */             }
/* 265:249 */             if (((DTReel.this.dt.direction[DTReel.this.unit] == -1 ? 1 : 0) & (DTReel.this.dt.td8e.line[DTReel.this.unit] > 33300 ? 1 : 0)) != 0)
/* 266:    */             {
/* 267:249 */               int tmp262_259 = DTReel.this.unit; int[] tmp262_252 = DTReel.this.dt.td8e.line;tmp262_252[tmp262_259] = ((int)(tmp262_252[tmp262_259] - 33300 * this.sleep / 1000.0D));
/* 268:    */             }
/* 269:    */           }
/* 270:252 */           if (DTReel.this.local_line != DTReel.this.dt.td8e.line[DTReel.this.unit])
/* 271:    */           {
/* 272:253 */             DTReel.this.local_line = DTReel.this.dt.td8e.line[DTReel.this.unit];
/* 273:254 */             DTReel.this.repaint();
/* 274:255 */             this.sleep = 20;
/* 275:    */           }
/* 276:257 */           Thread.sleep(this.sleep);
/* 277:258 */           this.sleep += 1;
/* 278:    */           
/* 279:260 */           continue;System.out.println("Turn error" + e);
/* 280:    */         }
/* 281:    */         catch (InterruptedException e) {}
/* 282:    */       }
/* 283:    */     }
/* 284:    */     
/* 285:    */     public Turn() {}
/* 286:    */   }
/* 287:    */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Devices.DTReel
 * JD-Core Version:    0.7.0.1
 */