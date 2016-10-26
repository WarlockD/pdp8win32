/*   1:    */ package Panel;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.awt.Dimension;
/*   5:    */ import java.awt.Font;
/*   6:    */ import java.awt.GridBagConstraints;
/*   7:    */ import java.awt.GridBagLayout;
/*   8:    */ import java.awt.event.ComponentAdapter;
/*   9:    */ import java.awt.event.ComponentEvent;
/*  10:    */ import javax.swing.JPanel;
/*  11:    */ 
/*  12:    */ public class AcLamps
/*  13:    */   extends JPanel
/*  14:    */ {
/*  15:    */   private XLamp acLamp0;
/*  16:    */   private XLamp acLamp1;
/*  17:    */   private XLamp acLamp10;
/*  18:    */   private XLamp acLamp11;
/*  19:    */   private XLamp acLamp2;
/*  20:    */   private XLamp acLamp3;
/*  21:    */   private XLamp acLamp4;
/*  22:    */   private XLamp acLamp5;
/*  23:    */   private XLamp acLamp6;
/*  24:    */   private XLamp acLamp7;
/*  25:    */   private XLamp acLamp8;
/*  26:    */   private XLamp acLamp9;
/*  27:    */   
/*  28:    */   public AcLamps()
/*  29:    */   {
/*  30: 18 */     initComponents();
/*  31:    */   }
/*  32:    */   
/*  33:    */   private void initComponents()
/*  34:    */   {
/*  35: 29 */     this.acLamp0 = new XLamp();
/*  36: 30 */     this.acLamp1 = new XLamp();
/*  37: 31 */     this.acLamp2 = new XLamp();
/*  38: 32 */     this.acLamp3 = new XLamp();
/*  39: 33 */     this.acLamp4 = new XLamp();
/*  40: 34 */     this.acLamp5 = new XLamp();
/*  41: 35 */     this.acLamp6 = new XLamp();
/*  42: 36 */     this.acLamp7 = new XLamp();
/*  43: 37 */     this.acLamp8 = new XLamp();
/*  44: 38 */     this.acLamp9 = new XLamp();
/*  45: 39 */     this.acLamp10 = new XLamp();
/*  46: 40 */     this.acLamp11 = new XLamp();
/*  47:    */     
/*  48: 42 */     setFont(new Font("Dialog", 0, 8));
/*  49: 43 */     setMaximumSize(new Dimension(312, 12));
/*  50: 44 */     setMinimumSize(new Dimension(156, 6));
/*  51: 45 */     setPreferredSize(new Dimension(312, 12));
/*  52: 46 */     addComponentListener(new ComponentAdapter()
/*  53:    */     {
/*  54:    */       public void componentResized(ComponentEvent evt)
/*  55:    */       {
/*  56: 48 */         AcLamps.this.formComponentResized(evt);
/*  57:    */       }
/*  58: 50 */     });
/*  59: 51 */     setLayout(new GridBagLayout());
/*  60:    */     
/*  61: 53 */     this.acLamp0.setBackground(new Color(193, 77, 28));
/*  62: 54 */     this.acLamp0.setForeground(new Color(0, 0, 0));
/*  63: 55 */     this.acLamp0.setDoubleBuffered(true);
/*  64: 56 */     this.acLamp0.setEdge(1);
/*  65: 57 */     this.acLamp0.setFont(new Font("Dialog", 1, 8));
/*  66: 58 */     this.acLamp0.setMaximumSize(new Dimension(26, 12));
/*  67: 59 */     this.acLamp0.setMinimumSize(new Dimension(13, 6));
/*  68: 60 */     this.acLamp0.setPreferredSize(new Dimension(26, 12));
/*  69: 61 */     this.acLamp0.setState(false);
/*  70: 62 */     this.acLamp0.setText("0");
/*  71: 63 */     add(this.acLamp0, new GridBagConstraints());
/*  72:    */     
/*  73: 65 */     this.acLamp1.setBackground(new Color(193, 77, 28));
/*  74: 66 */     this.acLamp1.setDoubleBuffered(true);
/*  75: 67 */     this.acLamp1.setEdge(1);
/*  76: 68 */     this.acLamp1.setFont(new Font("Dialog", 1, 8));
/*  77: 69 */     this.acLamp1.setMaximumSize(new Dimension(26, 12));
/*  78: 70 */     this.acLamp1.setMinimumSize(new Dimension(13, 6));
/*  79: 71 */     this.acLamp1.setPreferredSize(new Dimension(26, 12));
/*  80: 72 */     this.acLamp1.setState(false);
/*  81: 73 */     this.acLamp1.setText("1");
/*  82: 74 */     add(this.acLamp1, new GridBagConstraints());
/*  83:    */     
/*  84: 76 */     this.acLamp2.setBackground(new Color(193, 77, 29));
/*  85: 77 */     this.acLamp2.setDoubleBuffered(true);
/*  86: 78 */     this.acLamp2.setEdge(1);
/*  87: 79 */     this.acLamp2.setFont(new Font("Dialog", 1, 8));
/*  88: 80 */     this.acLamp2.setMaximumSize(new Dimension(26, 12));
/*  89: 81 */     this.acLamp2.setMinimumSize(new Dimension(13, 6));
/*  90: 82 */     this.acLamp2.setPreferredSize(new Dimension(26, 12));
/*  91: 83 */     this.acLamp2.setState(false);
/*  92: 84 */     this.acLamp2.setText("2");
/*  93: 85 */     add(this.acLamp2, new GridBagConstraints());
/*  94:    */     
/*  95: 87 */     this.acLamp3.setBackground(new Color(193, 132, 28));
/*  96: 88 */     this.acLamp3.setDoubleBuffered(true);
/*  97: 89 */     this.acLamp3.setEdge(1);
/*  98: 90 */     this.acLamp3.setFont(new Font("Dialog", 1, 8));
/*  99: 91 */     this.acLamp3.setMaximumSize(new Dimension(26, 12));
/* 100: 92 */     this.acLamp3.setMinimumSize(new Dimension(13, 6));
/* 101: 93 */     this.acLamp3.setPreferredSize(new Dimension(26, 12));
/* 102: 94 */     this.acLamp3.setState(false);
/* 103: 95 */     this.acLamp3.setText("3");
/* 104: 96 */     add(this.acLamp3, new GridBagConstraints());
/* 105:    */     
/* 106: 98 */     this.acLamp4.setBackground(new Color(193, 132, 28));
/* 107: 99 */     this.acLamp4.setDoubleBuffered(true);
/* 108:100 */     this.acLamp4.setEdge(1);
/* 109:101 */     this.acLamp4.setFont(new Font("Dialog", 1, 8));
/* 110:102 */     this.acLamp4.setMaximumSize(new Dimension(26, 12));
/* 111:103 */     this.acLamp4.setMinimumSize(new Dimension(13, 6));
/* 112:104 */     this.acLamp4.setPreferredSize(new Dimension(26, 12));
/* 113:105 */     this.acLamp4.setState(false);
/* 114:106 */     this.acLamp4.setText("4");
/* 115:107 */     add(this.acLamp4, new GridBagConstraints());
/* 116:    */     
/* 117:109 */     this.acLamp5.setBackground(new Color(193, 132, 28));
/* 118:110 */     this.acLamp5.setDoubleBuffered(true);
/* 119:111 */     this.acLamp5.setEdge(1);
/* 120:112 */     this.acLamp5.setFont(new Font("Dialog", 1, 8));
/* 121:113 */     this.acLamp5.setMaximumSize(new Dimension(26, 12));
/* 122:114 */     this.acLamp5.setMinimumSize(new Dimension(13, 6));
/* 123:115 */     this.acLamp5.setPreferredSize(new Dimension(26, 12));
/* 124:116 */     this.acLamp5.setState(false);
/* 125:117 */     this.acLamp5.setText("5");
/* 126:118 */     add(this.acLamp5, new GridBagConstraints());
/* 127:    */     
/* 128:120 */     this.acLamp6.setBackground(new Color(193, 77, 28));
/* 129:121 */     this.acLamp6.setDoubleBuffered(true);
/* 130:122 */     this.acLamp6.setEdge(1);
/* 131:123 */     this.acLamp6.setFont(new Font("Dialog", 1, 8));
/* 132:124 */     this.acLamp6.setMaximumSize(new Dimension(26, 12));
/* 133:125 */     this.acLamp6.setMinimumSize(new Dimension(13, 6));
/* 134:126 */     this.acLamp6.setPreferredSize(new Dimension(26, 12));
/* 135:127 */     this.acLamp6.setState(false);
/* 136:128 */     this.acLamp6.setText("6");
/* 137:129 */     add(this.acLamp6, new GridBagConstraints());
/* 138:    */     
/* 139:131 */     this.acLamp7.setBackground(new Color(193, 77, 28));
/* 140:132 */     this.acLamp7.setDoubleBuffered(true);
/* 141:133 */     this.acLamp7.setEdge(1);
/* 142:134 */     this.acLamp7.setFont(new Font("Dialog", 1, 8));
/* 143:135 */     this.acLamp7.setMaximumSize(new Dimension(26, 12));
/* 144:136 */     this.acLamp7.setMinimumSize(new Dimension(13, 6));
/* 145:137 */     this.acLamp7.setPreferredSize(new Dimension(26, 12));
/* 146:138 */     this.acLamp7.setState(false);
/* 147:139 */     this.acLamp7.setText("7");
/* 148:140 */     add(this.acLamp7, new GridBagConstraints());
/* 149:    */     
/* 150:142 */     this.acLamp8.setBackground(new Color(193, 77, 28));
/* 151:143 */     this.acLamp8.setDoubleBuffered(true);
/* 152:144 */     this.acLamp8.setEdge(1);
/* 153:145 */     this.acLamp8.setFont(new Font("Dialog", 1, 8));
/* 154:146 */     this.acLamp8.setMaximumSize(new Dimension(26, 12));
/* 155:147 */     this.acLamp8.setMinimumSize(new Dimension(13, 6));
/* 156:148 */     this.acLamp8.setPreferredSize(new Dimension(26, 12));
/* 157:149 */     this.acLamp8.setState(false);
/* 158:150 */     this.acLamp8.setText("8");
/* 159:151 */     add(this.acLamp8, new GridBagConstraints());
/* 160:    */     
/* 161:153 */     this.acLamp9.setBackground(new Color(193, 132, 28));
/* 162:154 */     this.acLamp9.setDoubleBuffered(true);
/* 163:155 */     this.acLamp9.setEdge(1);
/* 164:156 */     this.acLamp9.setFont(new Font("Dialog", 1, 8));
/* 165:157 */     this.acLamp9.setMaximumSize(new Dimension(26, 12));
/* 166:158 */     this.acLamp9.setMinimumSize(new Dimension(13, 6));
/* 167:159 */     this.acLamp9.setPreferredSize(new Dimension(26, 12));
/* 168:160 */     this.acLamp9.setState(false);
/* 169:161 */     this.acLamp9.setText("9");
/* 170:162 */     add(this.acLamp9, new GridBagConstraints());
/* 171:    */     
/* 172:164 */     this.acLamp10.setBackground(new Color(193, 132, 28));
/* 173:165 */     this.acLamp10.setDoubleBuffered(true);
/* 174:166 */     this.acLamp10.setEdge(1);
/* 175:167 */     this.acLamp10.setFont(new Font("Dialog", 1, 8));
/* 176:168 */     this.acLamp10.setMaximumSize(new Dimension(26, 12));
/* 177:169 */     this.acLamp10.setMinimumSize(new Dimension(13, 6));
/* 178:170 */     this.acLamp10.setPreferredSize(new Dimension(26, 12));
/* 179:171 */     this.acLamp10.setState(false);
/* 180:172 */     this.acLamp10.setText("10");
/* 181:173 */     add(this.acLamp10, new GridBagConstraints());
/* 182:    */     
/* 183:175 */     this.acLamp11.setBackground(new Color(193, 132, 28));
/* 184:176 */     this.acLamp11.setDoubleBuffered(true);
/* 185:177 */     this.acLamp11.setEdge(1);
/* 186:178 */     this.acLamp11.setFont(new Font("Dialog", 1, 8));
/* 187:179 */     this.acLamp11.setMaximumSize(new Dimension(26, 12));
/* 188:180 */     this.acLamp11.setMinimumSize(new Dimension(13, 6));
/* 189:181 */     this.acLamp11.setPreferredSize(new Dimension(26, 12));
/* 190:182 */     this.acLamp11.setState(false);
/* 191:183 */     this.acLamp11.setText("11");
/* 192:184 */     add(this.acLamp11, new GridBagConstraints());
/* 193:    */   }
/* 194:    */   
/* 195:    */   private void formComponentResized(ComponentEvent evt)
/* 196:    */   {
/* 197:188 */     if (getWidth() < 300)
/* 198:    */     {
/* 199:189 */       this.acLamp0.setFont(new Font("Dialog", 1, 4));
/* 200:190 */       this.acLamp1.setFont(new Font("Dialog", 1, 4));
/* 201:191 */       this.acLamp2.setFont(new Font("Dialog", 1, 4));
/* 202:192 */       this.acLamp3.setFont(new Font("Dialog", 1, 4));
/* 203:193 */       this.acLamp4.setFont(new Font("Dialog", 1, 4));
/* 204:194 */       this.acLamp5.setFont(new Font("Dialog", 1, 4));
/* 205:195 */       this.acLamp6.setFont(new Font("Dialog", 1, 4));
/* 206:196 */       this.acLamp7.setFont(new Font("Dialog", 1, 4));
/* 207:197 */       this.acLamp8.setFont(new Font("Dialog", 1, 4));
/* 208:198 */       this.acLamp9.setFont(new Font("Dialog", 1, 4));
/* 209:199 */       this.acLamp10.setFont(new Font("Dialog", 1, 4));
/* 210:200 */       this.acLamp11.setFont(new Font("Dialog", 1, 4));
/* 211:    */     }
/* 212:    */     else
/* 213:    */     {
/* 214:202 */       this.acLamp0.setFont(new Font("Dialog", 1, 8));
/* 215:203 */       this.acLamp1.setFont(new Font("Dialog", 1, 8));
/* 216:204 */       this.acLamp2.setFont(new Font("Dialog", 1, 8));
/* 217:205 */       this.acLamp3.setFont(new Font("Dialog", 1, 8));
/* 218:206 */       this.acLamp4.setFont(new Font("Dialog", 1, 8));
/* 219:207 */       this.acLamp5.setFont(new Font("Dialog", 1, 8));
/* 220:208 */       this.acLamp6.setFont(new Font("Dialog", 1, 8));
/* 221:209 */       this.acLamp7.setFont(new Font("Dialog", 1, 8));
/* 222:210 */       this.acLamp8.setFont(new Font("Dialog", 1, 8));
/* 223:211 */       this.acLamp9.setFont(new Font("Dialog", 1, 8));
/* 224:212 */       this.acLamp10.setFont(new Font("Dialog", 1, 8));
/* 225:213 */       this.acLamp11.setFont(new Font("Dialog", 1, 8));
/* 226:    */     }
/* 227:    */   }
/* 228:    */   
/* 229:    */   public void setState(int state)
/* 230:    */   {
/* 231:223 */     if (this.state != state)
/* 232:    */     {
/* 233:224 */       this.state = state;
/* 234:225 */       this.acLamp0.setState(state & 0x800);
/* 235:226 */       this.acLamp1.setState(state & 0x400);
/* 236:227 */       this.acLamp2.setState(state & 0x200);
/* 237:228 */       this.acLamp3.setState(state & 0x100);
/* 238:229 */       this.acLamp4.setState(state & 0x80);
/* 239:230 */       this.acLamp5.setState(state & 0x40);
/* 240:231 */       this.acLamp6.setState(state & 0x20);
/* 241:232 */       this.acLamp7.setState(state & 0x10);
/* 242:233 */       this.acLamp8.setState(state & 0x8);
/* 243:234 */       this.acLamp9.setState(state & 0x4);
/* 244:235 */       this.acLamp10.setState(state & 0x2);
/* 245:236 */       this.acLamp11.setState(state & 0x1);
/* 246:    */     }
/* 247:    */   }
/* 248:    */   
/* 249:260 */   private int state = -1;
/* 250:    */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Panel.AcLamps
 * JD-Core Version:    0.7.0.1
 */