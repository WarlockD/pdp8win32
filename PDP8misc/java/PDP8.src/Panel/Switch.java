/*   1:    */ package Panel;
/*   2:    */ 
/*   3:    */ import java.awt.BasicStroke;
/*   4:    */ import java.awt.Color;
/*   5:    */ import java.awt.Dimension;
/*   6:    */ import java.awt.Graphics;
/*   7:    */ import java.awt.Graphics2D;
/*   8:    */ import java.awt.Polygon;
/*   9:    */ import java.awt.RenderingHints;
/*  10:    */ import java.awt.Stroke;
/*  11:    */ import java.awt.event.FocusEvent;
/*  12:    */ import java.awt.event.FocusListener;
/*  13:    */ import java.awt.event.MouseAdapter;
/*  14:    */ import java.awt.event.MouseEvent;
/*  15:    */ import java.beans.PropertyChangeListener;
/*  16:    */ import java.beans.PropertyChangeSupport;
/*  17:    */ import javax.swing.JComponent;
/*  18:    */ 
/*  19:    */ public class Switch
/*  20:    */   extends JComponent
/*  21:    */ {
/*  22:    */   private int size;
/*  23: 26 */   private boolean start = true;
/*  24: 27 */   private boolean pos = true;
/*  25: 28 */   private boolean truepos = true;
/*  26: 29 */   private boolean toggle = false;
/*  27:    */   private Color back;
/*  28:    */   private Color dark;
/*  29: 33 */   private static final Dimension MIN_SIZE = new Dimension(13, 22);
/*  30: 34 */   private static final Dimension PREF_SIZE = new Dimension(26, 44);
/*  31: 37 */   private static final RenderingHints AALIAS = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*  32: 40 */   Stroke drawingStroke = new BasicStroke(1.0F);
/*  33:    */   protected transient PropertyChangeSupport pchglisteners;
/*  34:    */   
/*  35:    */   public Switch()
/*  36:    */   {
/*  37: 46 */     setPreferredSize(PREF_SIZE);
/*  38: 47 */     setMinimumSize(MIN_SIZE);
/*  39: 48 */     setBackground(new Color(193, 132, 29));
/*  40: 49 */     addMouseListener(new MouseAdapter()
/*  41:    */     {
/*  42:    */       public void mouseClicked(MouseEvent me)
/*  43:    */       {
/*  44: 53 */         if (Switch.this.toggle) {
/*  45: 53 */           Switch.this.toggle();
/*  46:    */         }
/*  47:    */       }
/*  48:    */       
/*  49:    */       public void mousePressed(MouseEvent me)
/*  50:    */       {
/*  51: 58 */         if (!Switch.this.toggle) {
/*  52: 58 */           Switch.this.depress(true);
/*  53:    */         }
/*  54:    */       }
/*  55:    */       
/*  56:    */       public void mouseReleased(MouseEvent me)
/*  57:    */       {
/*  58: 63 */         if (!Switch.this.toggle) {
/*  59: 63 */           Switch.this.depress(false);
/*  60:    */         }
/*  61:    */       }
/*  62: 67 */     });
/*  63: 68 */     addFocusListener(new FocusListener()
/*  64:    */     {
/*  65:    */       public void focusGained(FocusEvent e)
/*  66:    */       {
/*  67: 70 */         Switch.this.repaint();
/*  68:    */       }
/*  69:    */       
/*  70:    */       public void focusLost(FocusEvent e)
/*  71:    */       {
/*  72: 73 */         Switch.this.repaint();
/*  73:    */       }
/*  74: 75 */     });
/*  75: 76 */     this.pchglisteners = new PropertyChangeSupport(this);
/*  76:    */   }
/*  77:    */   
/*  78:    */   private void depress(boolean press)
/*  79:    */   {
/*  80: 81 */     if (press)
/*  81:    */     {
/*  82: 82 */       boolean newpos = !this.start;
/*  83: 83 */       setPos(newpos);
/*  84:    */     }
/*  85:    */     else
/*  86:    */     {
/*  87: 86 */       setPos(this.start);
/*  88:    */     }
/*  89:    */   }
/*  90:    */   
/*  91:    */   private void toggle()
/*  92:    */   {
/*  93: 91 */     setPos(!this.pos);
/*  94:    */   }
/*  95:    */   
/*  96:    */   public boolean getPos()
/*  97:    */   {
/*  98: 95 */     return this.pos;
/*  99:    */   }
/* 100:    */   
/* 101:    */   public void setPos(boolean pos)
/* 102:    */   {
/* 103: 99 */     boolean oldvalue = this.pos;
/* 104:100 */     this.pos = pos;
/* 105:101 */     repaint();
/* 106:102 */     this.pchglisteners.firePropertyChange(getName(), this.truepos ^ oldvalue, this.truepos ^ this.pos);
/* 107:    */   }
/* 108:    */   
/* 109:    */   public boolean getToggle()
/* 110:    */   {
/* 111:106 */     return this.toggle;
/* 112:    */   }
/* 113:    */   
/* 114:    */   public void setToggle(boolean toggle)
/* 115:    */   {
/* 116:110 */     this.toggle = toggle;
/* 117:    */   }
/* 118:    */   
/* 119:    */   public boolean getInitial()
/* 120:    */   {
/* 121:114 */     return this.start;
/* 122:    */   }
/* 123:    */   
/* 124:    */   public void setInitial(boolean start)
/* 125:    */   {
/* 126:118 */     this.start = start;
/* 127:119 */     this.pos = start;
/* 128:120 */     repaint();
/* 129:    */   }
/* 130:    */   
/* 131:    */   public void setTruepos(boolean truepos)
/* 132:    */   {
/* 133:124 */     this.truepos = truepos;
/* 134:    */   }
/* 135:    */   
/* 136:    */   public boolean getTruepos()
/* 137:    */   {
/* 138:128 */     return this.truepos;
/* 139:    */   }
/* 140:    */   
/* 141:    */   public Color getBackground()
/* 142:    */   {
/* 143:133 */     return this.back;
/* 144:    */   }
/* 145:    */   
/* 146:    */   public void setBackground(Color back)
/* 147:    */   {
/* 148:138 */     this.back = back;
/* 149:139 */     this.dark = back.darker();
/* 150:    */   }
/* 151:    */   
/* 152:    */   public void addPropertyChangeListener(PropertyChangeListener pl)
/* 153:    */   {
/* 154:144 */     this.pchglisteners.addPropertyChangeListener(pl);
/* 155:    */   }
/* 156:    */   
/* 157:    */   public void removePropertyChangeListener(PropertyChangeListener pl)
/* 158:    */   {
/* 159:149 */     this.pchglisteners.removePropertyChangeListener(pl);
/* 160:    */   }
/* 161:    */   
/* 162:    */   public void paint(Graphics g)
/* 163:    */   {
/* 164:155 */     int width = getWidth();
/* 165:156 */     int height = getHeight();
/* 166:    */     
/* 167:158 */     Graphics2D g2d = (Graphics2D)g;
/* 168:    */      <Polygon Points="0,0 -1,0 20.8,11 8.8,11 0,0" Stroke="White" ></Polygon>
/* 169:160 */     g2d.addRenderingHints(AALIAS);
/* 170:163 */     if (this.pos)
/* 171:    */     {
/* 172:164 */       g.setColor(this.back);
/* 173:165 */       Polygon p1 = new Polygon();
/* 174:166 */       p1.addPoint(0, 0);
/* 175:167 */       p1.addPoint(width - 1, 0);
/* 176:168 */       p1.addPoint((int)(width * 0.8F), (int)(height * 0.25D));
/* 177:169 */       p1.addPoint((int)(width * 0.2F), (int)(height * 0.25D));
/* 178:170 */       p1.addPoint(0, 0);
/* 179:171 */       g.fillPolygon(p1);
/* 180:172 */       g2d.setColor(Color.WHITE);
/* 181:173 */       g2d.drawPolygon(p1);
/* 182:174 */       g.setColor(this.back);
/* 183:175 */       Polygon p2 = new Polygon();
/* 184:176 */       p2.addPoint((int)(width * 0.8F), (int)(height * 0.25D));
/* 185:177 */       p2.addPoint((int)(width * 0.8F), (int)(height * 0.4D));
/* 186:178 */       p2.addPoint((int)(width * 0.2F), (int)(height * 0.4D));
/* 187:179 */       p2.addPoint((int)(width * 0.2F), (int)(height * 0.25D));
/* 188:180 */       g.fillPolygon(p2);
/* 189:181 */       g2d.setColor(Color.WHITE);
/* 190:182 */       g2d.drawPolygon(p2);
/* 191:183 */       g.setColor(this.dark);
/* 192:184 */       Polygon p3 = new Polygon();
/* 193:185 */       p3.addPoint(0, 0);
/* 194:186 */       p3.addPoint((int)(width * 0.2F), (int)(height * 0.25D));
/* 195:187 */       p3.addPoint((int)(width * 0.2F), (int)(height * 0.4D));
/* 196:188 */       p3.addPoint(0, (int)(height * 0.5D));
/* 197:189 */       p3.addPoint(0, 0);
/* 198:190 */       g.fillPolygon(p3);
/* 199:191 */       g2d.setColor(Color.WHITE);
/* 200:192 */       g2d.drawPolygon(p3);
/* 201:193 */       g.setColor(this.dark);
/* 202:194 */       Polygon p4 = new Polygon();
/* 203:195 */       p4.addPoint(width - 1, 0);
/* 204:196 */       p4.addPoint(width - 1, (int)(height * 0.5D));
/* 205:197 */       p4.addPoint((int)(width * 0.8F), (int)(height * 0.4D));
/* 206:198 */       p4.addPoint((int)(width * 0.8F), (int)(height * 0.25D));
/* 207:199 */       p4.addPoint(width - 1, 0);
/* 208:200 */       g.fillPolygon(p4);
/* 209:201 */       g2d.setColor(Color.WHITE);
/* 210:202 */       g2d.drawPolygon(p4);
/* 211:203 */       g.setColor(this.dark);
/* 212:204 */       Polygon p5 = new Polygon();
/* 213:205 */       p5.addPoint(width - 1, (int)(height * 0.5D));
/* 214:206 */       p5.addPoint(width - 1, height - 1);
/* 215:207 */       p5.addPoint(0, height - 1);
/* 216:208 */       p5.addPoint(0, (int)(height * 0.5D));
/* 217:209 */       p5.addPoint((int)(width * 0.2F), (int)(height * 0.4D));
/* 218:210 */       p5.addPoint((int)(width * 0.8F), (int)(height * 0.4D));
/* 219:211 */       p5.addPoint(width - 1, (int)(height * 0.5D));
/* 220:212 */       g.fillPolygon(p5);
/* 221:213 */       g2d.setColor(Color.WHITE);
/* 222:214 */       g2d.drawPolygon(p5);
/* 223:215 */       Polygon p6 = new Polygon();
/* 224:216 */       p6.addPoint(width - 1, (int)(height * 0.5D));
/* 225:217 */       p6.addPoint(0, (int)(height * 0.5D));
/* 226:218 */       p6.addPoint((int)(width * 0.2F), (int)(height * 0.4D));
/* 227:219 */       p6.addPoint((int)(width * 0.8F), (int)(height * 0.4D));
/* 228:220 */       p6.addPoint(width - 1, (int)(height * 0.5D));
/* 229:221 */       g2d.setColor(Color.WHITE);
/* 230:222 */       g2d.drawPolygon(p6);
/* 231:    */     }
/* 232:    */     else
/* 233:    */     {
/* 234:224 */       g.setColor(this.back);
/* 235:225 */       Polygon p1 = new Polygon();
/* 236:226 */       p1.addPoint(0, 0);
/* 237:227 */       p1.addPoint(width - 1, 0);
/* 238:228 */       p1.addPoint((int)(width * 0.8F), (int)(height * 0.8D));
/* 239:229 */       p1.addPoint((int)(width * 0.2F), (int)(height * 0.8D));
/* 240:230 */       p1.addPoint(0, 0);
/* 241:231 */       g.fillPolygon(p1);
/* 242:232 */       g2d.setColor(Color.WHITE);
/* 243:233 */       g2d.drawPolygon(p1);
/* 244:234 */       g.setColor(this.back);
/* 245:235 */       Polygon p2 = new Polygon();
/* 246:236 */       p2.addPoint((int)(width * 0.8F), (int)(height * 0.8D));
/* 247:237 */       p2.addPoint((int)(width * 0.8F), (int)(height * 0.9D));
/* 248:238 */       p2.addPoint((int)(width * 0.2F), (int)(height * 0.9D));
/* 249:239 */       p2.addPoint((int)(width * 0.2F), (int)(height * 0.8D));
/* 250:240 */       g.fillPolygon(p2);
/* 251:241 */       g2d.setColor(Color.WHITE);
/* 252:242 */       g2d.drawPolygon(p2);
/* 253:243 */       g.setColor(this.dark);
/* 254:244 */       Polygon p3 = new Polygon();
/* 255:245 */       p3.addPoint(0, 0);
/* 256:246 */       p3.addPoint((int)(width * 0.2F), (int)(height * 0.8D));
/* 257:247 */       p3.addPoint((int)(width * 0.2F), (int)(height * 0.9D));
/* 258:248 */       p3.addPoint(0, height - 1);
/* 259:249 */       p3.addPoint(0, 0);
/* 260:250 */       g.fillPolygon(p3);
/* 261:251 */       g2d.setColor(Color.WHITE);
/* 262:252 */       g2d.drawPolygon(p3);
/* 263:253 */       g.setColor(this.dark);
/* 264:254 */       Polygon p4 = new Polygon();
/* 265:255 */       p4.addPoint(width - 1, 0);
/* 266:256 */       p4.addPoint(width - 1, height - 1);
/* 267:257 */       p4.addPoint((int)(width * 0.8F), (int)(height * 0.9D));
/* 268:258 */       p4.addPoint((int)(width * 0.8F), (int)(height * 0.8D));
/* 269:259 */       p4.addPoint(width - 1, 0);
/* 270:260 */       g.fillPolygon(p4);
/* 271:261 */       g2d.setColor(Color.WHITE);
/* 272:262 */       g2d.drawPolygon(p4);
/* 273:263 */       g.setColor(this.dark);
/* 274:264 */       Polygon p5 = new Polygon();
/* 275:265 */       p5.addPoint(width - 1, (int)(height * 0.5D));
/* 276:266 */       p5.addPoint(width - 1, height - 1);
/* 277:267 */       p5.addPoint(0, height - 1);
/* 278:268 */       p5.addPoint(0, (int)(height * 0.5D));
/* 279:269 */       p5.addPoint((int)(width * 0.2F), (int)(height * 0.9D));
/* 280:270 */       p5.addPoint((int)(width * 0.8F), (int)(height * 0.9D));
/* 281:271 */       p5.addPoint(width - 1, (int)(height * 0.5D));
/* 282:272 */       g.fillPolygon(p5);
/* 283:273 */       g2d.setColor(Color.WHITE);
/* 284:274 */       g2d.drawPolygon(p5);
/* 285:    */     }
/* 286:277 */     paintBorder(g);
/* 287:    */   }
/* 288:    */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Panel.Switch
 * JD-Core Version:    0.7.0.1
 */