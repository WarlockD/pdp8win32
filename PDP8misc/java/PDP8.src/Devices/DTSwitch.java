/*   1:    */ package Devices;
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
/*  15:    */ import java.awt.geom.AffineTransform;
/*  16:    */ import java.beans.PropertyChangeListener;
/*  17:    */ import java.beans.PropertyChangeSupport;
/*  18:    */ import javax.swing.JComponent;
/*  19:    */ 
/*  20:    */ public class DTSwitch
/*  21:    */   extends JComponent
/*  22:    */ {
/*  23:    */   private int size;
/*  24: 22 */   private int start = 0;
/*  25: 23 */   private int pos = 0;
/*  26:    */   private boolean pos3;
/*  27:    */   private boolean mom;
/*  28:    */   private boolean horizontal;
/*  29:    */   private Color back;
/*  30:    */   private Color dark;
/*  31:    */   private AffineTransform a;
/*  32: 30 */   private static final Dimension MIN_SIZE = new Dimension(13, 22);
/*  33: 31 */   private static final Dimension PREF_SIZE = new Dimension(26, 44);
/*  34: 33 */   private static final RenderingHints AALIAS = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*  35: 36 */   Stroke drawingStroke = new BasicStroke(1.0F);
/*  36:    */   protected transient PropertyChangeSupport pchglisteners;
/*  37:    */   
/*  38:    */   public DTSwitch()
/*  39:    */   {
/*  40: 42 */     setPreferredSize(PREF_SIZE);
/*  41: 43 */     setMinimumSize(MIN_SIZE);
/*  42: 44 */     setBackground(new Color(100, 100, 100));
/*  43: 45 */     addMouseListener(new MouseAdapter()
/*  44:    */     {
/*  45:    */       public void mousePressed(MouseEvent me)
/*  46:    */       {
/*  47: 54 */         DTSwitch.this.toggle(me);
/*  48:    */       }
/*  49:    */       
/*  50:    */       public void mouseReleased(MouseEvent me)
/*  51:    */       {
/*  52: 59 */         DTSwitch.this.toggle(me);
/*  53:    */       }
/*  54: 63 */     });
/*  55: 64 */     addFocusListener(new FocusListener()
/*  56:    */     {
/*  57:    */       public void focusGained(FocusEvent e)
/*  58:    */       {
/*  59: 67 */         DTSwitch.this.repaint();
/*  60:    */       }
/*  61:    */       
/*  62:    */       public void focusLost(FocusEvent e)
/*  63:    */       {
/*  64: 71 */         DTSwitch.this.repaint();
/*  65:    */       }
/*  66: 73 */     });
/*  67: 74 */     this.pchglisteners = new PropertyChangeSupport(this);
/*  68:    */   }
/*  69:    */   
/*  70:    */   private void toggle(MouseEvent me)
/*  71:    */   {
/*  72: 78 */     if ((me.getID() == 502 & this.mom))
/*  73:    */     {
/*  74: 79 */       if (this.pos3) {
/*  75: 79 */         setPos(0);
/*  76:    */       }
/*  77: 80 */       if (!this.pos3) {
/*  78: 80 */         setPos(2);
/*  79:    */       }
/*  80:    */       return;
/*  81:    */     }
/*  82:    */     int height;
/*  83:    */     int click;
/*  84:    */     int height;
/*  85: 86 */     if (this.horizontal)
/*  86:    */     {
/*  87: 87 */       int click = me.getX();
/*  88: 88 */       height = getWidth();
/*  89:    */     }
/*  90:    */     else
/*  91:    */     {
/*  92: 90 */       click = me.getY();
/*  93: 91 */       height = getHeight();
/*  94:    */     }
/*  95:    */     int tpos;
/*  96:    */     int tpos;
/*  97: 93 */     if (this.pos3)
/*  98:    */     {
/*  99:    */       int tpos;
/* 100: 94 */       if (click < height / 3)
/* 101:    */       {
/* 102: 95 */         tpos = 2;
/* 103:    */       }
/* 104:    */       else
/* 105:    */       {
/* 106:    */         int tpos;
/* 107: 96 */         if (click > 2 * height / 3) {
/* 108: 97 */           tpos = 1;
/* 109:    */         } else {
/* 110: 99 */           tpos = 0;
/* 111:    */         }
/* 112:    */       }
/* 113:    */     }
/* 114:    */     else
/* 115:    */     {
/* 116:    */       int tpos;
/* 117:102 */       if (click < height / 2) {
/* 118:103 */         tpos = 2;
/* 119:    */       } else {
/* 120:105 */         tpos = 1;
/* 121:    */       }
/* 122:    */     }
/* 123:108 */     setPos(tpos);
/* 124:    */   }
/* 125:    */   
/* 126:    */   public int getPos()
/* 127:    */   {
/* 128:112 */     return this.pos;
/* 129:    */   }
/* 130:    */   
/* 131:    */   public void setPos(int pos)
/* 132:    */   {
/* 133:116 */     int oldvalue = this.pos;
/* 134:117 */     this.pos = pos;
/* 135:118 */     repaint();
/* 136:119 */     this.pchglisteners.firePropertyChange(getName(), oldvalue, this.pos);
/* 137:    */   }
/* 138:    */   
/* 139:    */   public boolean getPos3()
/* 140:    */   {
/* 141:123 */     return this.pos3;
/* 142:    */   }
/* 143:    */   
/* 144:    */   public void setPos3(boolean pos3)
/* 145:    */   {
/* 146:127 */     this.pos3 = pos3;
/* 147:128 */     repaint();
/* 148:    */   }
/* 149:    */   
/* 150:    */   public boolean getMomentary()
/* 151:    */   {
/* 152:132 */     return this.mom;
/* 153:    */   }
/* 154:    */   
/* 155:    */   public void setMomentary(boolean mom)
/* 156:    */   {
/* 157:136 */     this.mom = mom;
/* 158:137 */     repaint();
/* 159:    */   }
/* 160:    */   
/* 161:    */   public boolean getHorizontal()
/* 162:    */   {
/* 163:141 */     return this.horizontal;
/* 164:    */   }
/* 165:    */   
/* 166:    */   public void setHorizontal(boolean hor)
/* 167:    */   {
/* 168:145 */     this.horizontal = hor;
/* 169:146 */     repaint();
/* 170:    */   }
/* 171:    */   
/* 172:    */   public int getInitial()
/* 173:    */   {
/* 174:150 */     return this.start;
/* 175:    */   }
/* 176:    */   
/* 177:    */   public void setInitial(int start)
/* 178:    */   {
/* 179:154 */     this.start = start;
/* 180:155 */     this.pos = start;
/* 181:156 */     repaint();
/* 182:    */   }
/* 183:    */   
/* 184:    */   public Color getBackground()
/* 185:    */   {
/* 186:161 */     return this.back;
/* 187:    */   }
/* 188:    */   
/* 189:    */   public void setBackground(Color back)
/* 190:    */   {
/* 191:166 */     this.back = back;
/* 192:167 */     this.dark = back.darker().darker();
/* 193:    */   }
/* 194:    */   
/* 195:    */   public void addPropertyChangeListener(PropertyChangeListener pl)
/* 196:    */   {
/* 197:172 */     this.pchglisteners.addPropertyChangeListener(pl);
/* 198:    */   }
/* 199:    */   
/* 200:    */   public void removePropertyChangeListener(PropertyChangeListener pl)
/* 201:    */   {
/* 202:177 */     this.pchglisteners.removePropertyChangeListener(pl);
/* 203:    */   }
/* 204:    */   
/* 205:    */   public void paint(Graphics g)
/* 206:    */   {
/* 207:183 */     int width = getWidth();
/* 208:184 */     int height = getHeight();
/* 209:185 */     Graphics2D g2d = (Graphics2D)g;
/* 210:186 */     this.a = AffineTransform.getRotateInstance(1.570796326794897D);
/* 211:187 */     this.a.scale(1.0D, -1.0D);
/* 212:188 */     if (this.horizontal)
/* 213:    */     {
/* 214:189 */       g2d.transform(this.a);
/* 215:190 */       height = getWidth();
/* 216:191 */       width = getHeight();
/* 217:    */     }
/* 218:193 */     g2d.addRenderingHints(AALIAS);
/* 219:194 */     if (this.pos == 0)
/* 220:    */     {
/* 221:195 */       OneSide(g, 0, 0, width, height / 2, 0.125F);
/* 222:196 */       OneSide(g, 0, height / 2, width, height / 2, 0.875F);
/* 223:    */     }
/* 224:197 */     else if (this.pos == 2)
/* 225:    */     {
/* 226:198 */       OneSide(g, 0, 0, width, height / 2 - height / 8, 0.07F);
/* 227:199 */       OneSide(g, 0, height / 2 - height / 8, width, height / 2 + height / 8, 0.7F);
/* 228:    */     }
/* 229:200 */     else if (this.pos == 1)
/* 230:    */     {
/* 231:201 */       OneSide(g, 0, 0, width, height / 2 + height / 8, 0.3F);
/* 232:202 */       OneSide(g, 0, height / 2 + height / 8, width, height / 2 - height / 8, 0.93F);
/* 233:    */     }
/* 234:204 */     paintBorder(g);
/* 235:    */   }
/* 236:    */   
/* 237:    */   private void OneSide(Graphics g, int x, int y, int width, int height, float pos)
/* 238:    */   {
/* 239:208 */     Graphics2D g2d = (Graphics2D)g;
/* 240:209 */     if (pos < 0.5F) {
/* 241:210 */       g.setColor(this.dark);
/* 242:    */     } else {
/* 243:212 */       g.setColor(this.back);
/* 244:    */     }
/* 245:214 */     Polygon p1 = new Polygon();
/* 246:215 */     p1.addPoint(x, y);
/* 247:216 */     p1.addPoint(x + width - 1, y);
/* 248:217 */     p1.addPoint((int)(x + width * 0.75D), (int)(y + height * pos));
/* 249:218 */     p1.addPoint((int)(x + width * 0.25D), (int)(y + height * pos));
/* 250:219 */     p1.addPoint(x, y);
/* 251:220 */     g.fillPolygon(p1);
/* 252:221 */     g2d.setColor(Color.WHITE);
/* 253:222 */     g.drawPolygon(p1);
/* 254:223 */     g.setColor(this.back);
/* 255:224 */     if (pos > 0.5F) {
/* 256:225 */       g.setColor(this.dark);
/* 257:    */     } else {
/* 258:227 */       g.setColor(this.back);
/* 259:    */     }
/* 260:229 */     Polygon p2 = new Polygon();
/* 261:230 */     p2.addPoint((int)(x + width * 0.75D), (int)(y + height * pos));
/* 262:231 */     p2.addPoint(x + width - 1, y + height);
/* 263:232 */     p2.addPoint(x, y + height);
/* 264:233 */     p2.addPoint((int)(x + width * 0.25D), (int)(y + height * pos));
/* 265:234 */     p2.addPoint((int)(x + width * 0.75D), (int)(y + height * pos));
/* 266:235 */     g.fillPolygon(p2);
/* 267:236 */     g2d.setColor(Color.WHITE);
/* 268:237 */     g.drawPolygon(p2);
/* 269:238 */     g.setColor(this.dark);
/* 270:239 */     Polygon p3 = new Polygon();
/* 271:240 */     p3.addPoint(x, y);
/* 272:241 */     p3.addPoint((int)(x + width * 0.25D), (int)(y + height * pos));
/* 273:242 */     p3.addPoint(x, y + height);
/* 274:243 */     p3.addPoint(x, y);
/* 275:244 */     g.fillPolygon(p3);
/* 276:245 */     g2d.setColor(Color.WHITE);
/* 277:246 */     g.drawPolygon(p3);
/* 278:247 */     g.setColor(this.dark);
/* 279:248 */     Polygon p4 = new Polygon();
/* 280:249 */     p4.addPoint(x + width - 1, y);
/* 281:250 */     p4.addPoint(x + width - 1, y + height);
/* 282:251 */     p4.addPoint((int)(x + width * 0.75D), (int)(y + height * pos));
/* 283:252 */     p4.addPoint(x + width - 1, y);
/* 284:253 */     g.fillPolygon(p4);
/* 285:254 */     g2d.setColor(Color.WHITE);
/* 286:255 */     g.drawPolygon(p4);
/* 287:    */   }
/* 288:    */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Devices.DTSwitch
 * JD-Core Version:    0.7.0.1
 */