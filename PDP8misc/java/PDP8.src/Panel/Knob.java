/*   1:    */ package Panel;
/*   2:    */ 
/*   3:    */ import java.awt.BasicStroke;
/*   4:    */ import java.awt.Color;
/*   5:    */ import java.awt.Dimension;
/*   6:    */ import java.awt.Graphics;
/*   7:    */ import java.awt.Graphics2D;
/*   8:    */ import java.awt.RenderingHints;
/*   9:    */ import java.awt.Stroke;
/*  10:    */ import java.awt.event.MouseAdapter;
/*  11:    */ import java.awt.event.MouseEvent;
/*  12:    */ import java.beans.PropertyChangeListener;
/*  13:    */ import java.beans.PropertyChangeSupport;
/*  14:    */ import javax.swing.JComponent;
/*  15:    */ 
/*  16:    */ public class Knob
/*  17:    */   extends JComponent
/*  18:    */ {
/*  19:    */   private static final float PI = 3.1415F;
/*  20:    */   private static final float MULTIP = 57.29747F;
/*  21:    */   private float start;
/*  22:    */   private float length;
/*  23:    */   private float start_ang;
/*  24:    */   private float length_ang;
/*  25:    */   private float size;
/*  26:    */   private int middlex;
/*  27:    */   private int middley;
/*  28:    */   private float mult;
/*  29:    */   private float a2;
/*  30: 36 */   private int ticks = 6;
/*  31:    */   private Color back;
/*  32:    */   private Color border_color;
/*  33: 40 */   private static final Dimension MIN_SIZE = new Dimension(50, 50);
/*  34: 41 */   private static final Dimension PREF_SIZE = new Dimension(100, 100);
/*  35: 44 */   private static final RenderingHints AALIAS = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*  36: 47 */   Stroke drawingStroke = new BasicStroke(2.0F);
/*  37:    */   protected transient PropertyChangeSupport knoblisteners;
/*  38: 53 */   private float ang = this.start_ang;
/*  39:    */   private float val;
/*  40:    */   private float startVal;
/*  41:    */   private double lastAng;
/*  42:    */   
/*  43:    */   public Knob()
/*  44:    */   {
/*  45: 61 */     setPreferredSize(PREF_SIZE);
/*  46: 62 */     this.knoblisteners = new PropertyChangeSupport(this);
/*  47: 63 */     addMouseListener(new MouseAdapter()
/*  48:    */     {
/*  49:    */       public void mouseClicked(MouseEvent me)
/*  50:    */       {
/*  51: 67 */         float xpos = me.getX() - Knob.this.middlex;
/*  52: 68 */         float ypos = me.getY() - Knob.this.middley;
/*  53: 69 */         double ang = Math.atan2(-ypos, xpos);
/*  54: 70 */         if (ang < 0.0D) {
/*  55: 70 */           ang += 6.282999992370606D;
/*  56:    */         }
/*  57: 71 */         double diff = Knob.this.start_ang - ang;
/*  58: 72 */         Knob.this.setValue((float)(diff / Knob.this.length_ang));
/*  59:    */       }
/*  60:    */     });
/*  61:    */   }
/*  62:    */   
/*  63:    */   public float getValue()
/*  64:    */   {
/*  65: 79 */     return this.val;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public void setValue(float val)
/*  69:    */   {
/*  70: 83 */     if (val < 0.0F) {
/*  71: 83 */       val = 0.0F;
/*  72:    */     }
/*  73: 84 */     if (val > 1.0F) {
/*  74: 84 */       val = 1.0F;
/*  75:    */     }
/*  76: 85 */     float oldval = this.val;
/*  77: 86 */     val = (float)Math.floor(val * (this.ticks - 1) + 0.5D) / (this.ticks - 1);
/*  78: 87 */     this.val = val;
/*  79: 88 */     this.ang = (this.start_ang - this.length_ang * val);
/*  80: 89 */     repaint();
/*  81: 90 */     this.knoblisteners.firePropertyChange(getName(), (int)(oldval * (this.ticks - 1)), (int)(this.val * (this.ticks - 1)));
/*  82:    */   }
/*  83:    */   
/*  84:    */   public float getStart()
/*  85:    */   {
/*  86: 94 */     return this.start;
/*  87:    */   }
/*  88:    */   
/*  89:    */   public void setStart(float start)
/*  90:    */   {
/*  91: 98 */     this.start = start;
/*  92: 99 */     this.start_ang = (start / 360.0F * 3.1415F * 2.0F);
/*  93:100 */     this.ang = this.start_ang;
/*  94:101 */     repaint();
/*  95:    */   }
/*  96:    */   
/*  97:    */   public float getLength()
/*  98:    */   {
/*  99:105 */     return this.length;
/* 100:    */   }
/* 101:    */   
/* 102:    */   public void setLength(float length)
/* 103:    */   {
/* 104:109 */     this.length = length;
/* 105:110 */     this.length_ang = (length / 360.0F * 3.1415F * 2.0F);
/* 106:111 */     repaint();
/* 107:    */   }
/* 108:    */   
/* 109:    */   public int getTicks()
/* 110:    */   {
/* 111:115 */     return this.ticks;
/* 112:    */   }
/* 113:    */   
/* 114:    */   public void setTicks(int ticks)
/* 115:    */   {
/* 116:119 */     this.ticks = ticks;
/* 117:120 */     repaint();
/* 118:    */   }
/* 119:    */   
/* 120:    */   public Color getBackground()
/* 121:    */   {
/* 122:125 */     return this.back;
/* 123:    */   }
/* 124:    */   
/* 125:    */   public void setBackground(Color back)
/* 126:    */   {
/* 127:130 */     this.back = back;
/* 128:    */   }
/* 129:    */   
/* 130:    */   public void addPropertyChangeListener(PropertyChangeListener pl)
/* 131:    */   {
/* 132:135 */     this.knoblisteners.addPropertyChangeListener(pl);
/* 133:    */   }
/* 134:    */   
/* 135:    */   public void removePropertyChangeListener(PropertyChangeListener pl)
/* 136:    */   {
/* 137:140 */     this.knoblisteners.removePropertyChangeListener(pl);
/* 138:    */   }
/* 139:    */   
/* 140:    */   public void paint(Graphics g)
/* 141:    */   {
/* 142:146 */     int width = getWidth();
/* 143:147 */     int height = getHeight();
/* 144:148 */     this.middlex = (width / 2);
/* 145:149 */     this.middley = (height / 2);
/* 146:150 */     if (width > height) {
/* 147:151 */       this.size = (height * 10 / 12);
/* 148:    */     } else {
/* 149:153 */       this.size = (width * 10 / 12);
/* 150:    */     }
/* 151:158 */     if ((g instanceof Graphics2D))
/* 152:    */     {
/* 153:159 */       Graphics2D g2d = (Graphics2D)g;
/* 154:160 */       g2d.setBackground(this.back);
/* 155:161 */       g2d.addRenderingHints(AALIAS);
/* 156:162 */       g2d.setStroke(this.drawingStroke);
/* 157:    */     }
/* 158:165 */     g.setColor(this.back);
/* 159:166 */     g.fillRect(0, 0, width, height);
/* 160:167 */     paintBorder(g);
/* 161:    */     
/* 162:169 */     g.setColor(Color.white);
/* 163:171 */     for (int tick = 0; tick < this.ticks; tick++)
/* 164:    */     {
/* 165:172 */       this.a2 = (this.start_ang - tick * this.length_ang / (this.ticks - 1));
/* 166:173 */       float x = (float)Math.cos(this.a2);
/* 167:174 */       float y = (float)Math.sin(this.a2);
/* 168:175 */       if (Math.abs(x) > Math.abs(y)) {
/* 169:176 */         this.mult = Math.abs(this.middlex / x);
/* 170:    */       } else {
/* 171:178 */         this.mult = Math.abs(this.middley / y);
/* 172:    */       }
/* 173:180 */       g.drawLine(this.middlex, this.middley, this.middlex + (int)(x * this.mult), this.middley - (int)(y * this.mult));
/* 174:    */     }
/* 175:183 */     g.setColor(Color.white);
/* 176:184 */     int s1 = (int)(this.size * 0.5D);int s2 = s1 * 2;
/* 177:185 */     g.fillOval(this.middlex - s1, this.middley - s1, s2, s2);
/* 178:    */     
/* 179:187 */     g.setColor(Color.black);
/* 180:188 */     int s3 = (int)(this.size * 0.45D);int s4 = s3 * 2;
/* 181:189 */     if (s4 >= s2)
/* 182:    */     {
/* 183:189 */       s1--;s4 = s3 * 2;
/* 184:    */     }
/* 185:190 */     g.fillOval(this.middlex - s3, this.middley - s3, s4, s4);
/* 186:    */     
/* 187:192 */     double x = this.middlex + this.size / 2.0F * Math.cos(this.ang);
/* 188:193 */     double y = this.middley - this.size / 2.0F * Math.sin(this.ang);
/* 189:194 */     g.setColor(Color.white);
/* 190:195 */     g.drawLine(this.middlex, this.middley, (int)x, (int)y);
/* 191:    */     
/* 192:197 */     g.setColor(Color.LIGHT_GRAY);
/* 193:198 */     int s5 = (int)(this.size * 0.3D);int s6 = s5 * 2;
/* 194:199 */     g.fillOval(this.middlex - s5, this.middley - s5, s6, s6);
/* 195:    */   }
/* 196:    */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Panel.Knob
 * JD-Core Version:    0.7.0.1
 */