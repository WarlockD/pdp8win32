/*   1:    */ package Panel;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.awt.Dimension;
/*   5:    */ import java.awt.Font;
/*   6:    */ import java.awt.GridBagConstraints;
/*   7:    */ import java.awt.GridBagLayout;
/*   8:    */ import javax.swing.JPanel;
/*   9:    */ 
/*  10:    */ public class MqLamps
/*  11:    */   extends JPanel
/*  12:    */ {
/*  13:    */   private XLamp mqLamp0;
/*  14:    */   private XLamp mqLamp1;
/*  15:    */   private XLamp mqLamp10;
/*  16:    */   private XLamp mqLamp11;
/*  17:    */   private XLamp mqLamp2;
/*  18:    */   private XLamp mqLamp3;
/*  19:    */   private XLamp mqLamp4;
/*  20:    */   private XLamp mqLamp5;
/*  21:    */   private XLamp mqLamp6;
/*  22:    */   private XLamp mqLamp7;
/*  23:    */   private XLamp mqLamp8;
/*  24:    */   private XLamp mqLamp9;
/*  25:    */   
/*  26:    */   public MqLamps()
/*  27:    */   {
/*  28: 18 */     initComponents();
/*  29:    */   }
/*  30:    */   
/*  31:    */   private void initComponents()
/*  32:    */   {
/*  33: 29 */     this.mqLamp0 = new XLamp();
/*  34: 30 */     this.mqLamp1 = new XLamp();
/*  35: 31 */     this.mqLamp2 = new XLamp();
/*  36: 32 */     this.mqLamp3 = new XLamp();
/*  37: 33 */     this.mqLamp4 = new XLamp();
/*  38: 34 */     this.mqLamp5 = new XLamp();
/*  39: 35 */     this.mqLamp6 = new XLamp();
/*  40: 36 */     this.mqLamp7 = new XLamp();
/*  41: 37 */     this.mqLamp8 = new XLamp();
/*  42: 38 */     this.mqLamp9 = new XLamp();
/*  43: 39 */     this.mqLamp10 = new XLamp();
/*  44: 40 */     this.mqLamp11 = new XLamp();
/*  45:    */     
/*  46: 42 */     setFont(new Font("Dialog", 0, 8));
/*  47: 43 */     setMaximumSize(new Dimension(312, 12));
/*  48: 44 */     setMinimumSize(new Dimension(156, 6));
/*  49: 45 */     setPreferredSize(new Dimension(312, 12));
/*  50: 46 */     setLayout(new GridBagLayout());
/*  51:    */     
/*  52: 48 */     this.mqLamp0.setBackground(new Color(193, 77, 28));
/*  53: 49 */     this.mqLamp0.setForeground(new Color(0, 0, 0));
/*  54: 50 */     this.mqLamp0.setDoubleBuffered(true);
/*  55: 51 */     this.mqLamp0.setEdge(1);
/*  56: 52 */     this.mqLamp0.setFont(new Font("Dialog", 1, 8));
/*  57: 53 */     this.mqLamp0.setMaximumSize(new Dimension(26, 12));
/*  58: 54 */     this.mqLamp0.setMinimumSize(new Dimension(13, 6));
/*  59: 55 */     this.mqLamp0.setPreferredSize(new Dimension(26, 12));
/*  60: 56 */     this.mqLamp0.setState(false);
/*  61: 57 */     this.mqLamp0.setText("");
/*  62: 58 */     add(this.mqLamp0, new GridBagConstraints());
/*  63:    */     
/*  64: 60 */     this.mqLamp1.setBackground(new Color(193, 77, 28));
/*  65: 61 */     this.mqLamp1.setDoubleBuffered(true);
/*  66: 62 */     this.mqLamp1.setEdge(1);
/*  67: 63 */     this.mqLamp1.setFont(new Font("Dialog", 1, 8));
/*  68: 64 */     this.mqLamp1.setMaximumSize(new Dimension(26, 12));
/*  69: 65 */     this.mqLamp1.setMinimumSize(new Dimension(13, 6));
/*  70: 66 */     this.mqLamp1.setPreferredSize(new Dimension(26, 12));
/*  71: 67 */     this.mqLamp1.setState(false);
/*  72: 68 */     this.mqLamp1.setText("");
/*  73: 69 */     add(this.mqLamp1, new GridBagConstraints());
/*  74:    */     
/*  75: 71 */     this.mqLamp2.setBackground(new Color(193, 77, 29));
/*  76: 72 */     this.mqLamp2.setDoubleBuffered(true);
/*  77: 73 */     this.mqLamp2.setEdge(1);
/*  78: 74 */     this.mqLamp2.setFont(new Font("Dialog", 1, 8));
/*  79: 75 */     this.mqLamp2.setMaximumSize(new Dimension(26, 12));
/*  80: 76 */     this.mqLamp2.setMinimumSize(new Dimension(13, 6));
/*  81: 77 */     this.mqLamp2.setPreferredSize(new Dimension(26, 12));
/*  82: 78 */     this.mqLamp2.setState(false);
/*  83: 79 */     this.mqLamp2.setText("");
/*  84: 80 */     add(this.mqLamp2, new GridBagConstraints());
/*  85:    */     
/*  86: 82 */     this.mqLamp3.setBackground(new Color(193, 132, 28));
/*  87: 83 */     this.mqLamp3.setDoubleBuffered(true);
/*  88: 84 */     this.mqLamp3.setEdge(1);
/*  89: 85 */     this.mqLamp3.setFont(new Font("Dialog", 1, 8));
/*  90: 86 */     this.mqLamp3.setMaximumSize(new Dimension(26, 12));
/*  91: 87 */     this.mqLamp3.setMinimumSize(new Dimension(13, 6));
/*  92: 88 */     this.mqLamp3.setPreferredSize(new Dimension(26, 12));
/*  93: 89 */     this.mqLamp3.setState(false);
/*  94: 90 */     this.mqLamp3.setText("");
/*  95: 91 */     add(this.mqLamp3, new GridBagConstraints());
/*  96:    */     
/*  97: 93 */     this.mqLamp4.setBackground(new Color(193, 132, 28));
/*  98: 94 */     this.mqLamp4.setDoubleBuffered(true);
/*  99: 95 */     this.mqLamp4.setEdge(1);
/* 100: 96 */     this.mqLamp4.setFont(new Font("Dialog", 1, 8));
/* 101: 97 */     this.mqLamp4.setMaximumSize(new Dimension(26, 12));
/* 102: 98 */     this.mqLamp4.setMinimumSize(new Dimension(13, 6));
/* 103: 99 */     this.mqLamp4.setPreferredSize(new Dimension(26, 12));
/* 104:100 */     this.mqLamp4.setState(false);
/* 105:101 */     this.mqLamp4.setText("");
/* 106:102 */     add(this.mqLamp4, new GridBagConstraints());
/* 107:    */     
/* 108:104 */     this.mqLamp5.setBackground(new Color(193, 132, 28));
/* 109:105 */     this.mqLamp5.setDoubleBuffered(true);
/* 110:106 */     this.mqLamp5.setEdge(1);
/* 111:107 */     this.mqLamp5.setFont(new Font("Dialog", 1, 8));
/* 112:108 */     this.mqLamp5.setMaximumSize(new Dimension(26, 12));
/* 113:109 */     this.mqLamp5.setMinimumSize(new Dimension(13, 6));
/* 114:110 */     this.mqLamp5.setPreferredSize(new Dimension(26, 12));
/* 115:111 */     this.mqLamp5.setState(false);
/* 116:112 */     this.mqLamp5.setText("");
/* 117:113 */     add(this.mqLamp5, new GridBagConstraints());
/* 118:    */     
/* 119:115 */     this.mqLamp6.setBackground(new Color(193, 77, 28));
/* 120:116 */     this.mqLamp6.setDoubleBuffered(true);
/* 121:117 */     this.mqLamp6.setEdge(1);
/* 122:118 */     this.mqLamp6.setFont(new Font("Dialog", 1, 8));
/* 123:119 */     this.mqLamp6.setMaximumSize(new Dimension(26, 12));
/* 124:120 */     this.mqLamp6.setMinimumSize(new Dimension(13, 6));
/* 125:121 */     this.mqLamp6.setPreferredSize(new Dimension(26, 12));
/* 126:122 */     this.mqLamp6.setState(false);
/* 127:123 */     this.mqLamp6.setText("");
/* 128:124 */     add(this.mqLamp6, new GridBagConstraints());
/* 129:    */     
/* 130:126 */     this.mqLamp7.setBackground(new Color(193, 77, 28));
/* 131:127 */     this.mqLamp7.setDoubleBuffered(true);
/* 132:128 */     this.mqLamp7.setEdge(1);
/* 133:129 */     this.mqLamp7.setFont(new Font("Dialog", 1, 8));
/* 134:130 */     this.mqLamp7.setMaximumSize(new Dimension(26, 12));
/* 135:131 */     this.mqLamp7.setMinimumSize(new Dimension(13, 6));
/* 136:132 */     this.mqLamp7.setPreferredSize(new Dimension(26, 12));
/* 137:133 */     this.mqLamp7.setState(false);
/* 138:134 */     this.mqLamp7.setText("");
/* 139:135 */     add(this.mqLamp7, new GridBagConstraints());
/* 140:    */     
/* 141:137 */     this.mqLamp8.setBackground(new Color(193, 77, 28));
/* 142:138 */     this.mqLamp8.setDoubleBuffered(true);
/* 143:139 */     this.mqLamp8.setEdge(1);
/* 144:140 */     this.mqLamp8.setFont(new Font("Dialog", 1, 8));
/* 145:141 */     this.mqLamp8.setMaximumSize(new Dimension(26, 12));
/* 146:142 */     this.mqLamp8.setMinimumSize(new Dimension(13, 6));
/* 147:143 */     this.mqLamp8.setPreferredSize(new Dimension(26, 12));
/* 148:144 */     this.mqLamp8.setState(false);
/* 149:145 */     this.mqLamp8.setText("");
/* 150:146 */     add(this.mqLamp8, new GridBagConstraints());
/* 151:    */     
/* 152:148 */     this.mqLamp9.setBackground(new Color(193, 132, 28));
/* 153:149 */     this.mqLamp9.setDoubleBuffered(true);
/* 154:150 */     this.mqLamp9.setEdge(1);
/* 155:151 */     this.mqLamp9.setFont(new Font("Dialog", 1, 8));
/* 156:152 */     this.mqLamp9.setMaximumSize(new Dimension(26, 12));
/* 157:153 */     this.mqLamp9.setMinimumSize(new Dimension(13, 6));
/* 158:154 */     this.mqLamp9.setPreferredSize(new Dimension(26, 12));
/* 159:155 */     this.mqLamp9.setState(false);
/* 160:156 */     this.mqLamp9.setText("");
/* 161:157 */     add(this.mqLamp9, new GridBagConstraints());
/* 162:    */     
/* 163:159 */     this.mqLamp10.setBackground(new Color(193, 132, 28));
/* 164:160 */     this.mqLamp10.setDoubleBuffered(true);
/* 165:161 */     this.mqLamp10.setEdge(1);
/* 166:162 */     this.mqLamp10.setFont(new Font("Dialog", 1, 8));
/* 167:163 */     this.mqLamp10.setMaximumSize(new Dimension(26, 12));
/* 168:164 */     this.mqLamp10.setMinimumSize(new Dimension(13, 6));
/* 169:165 */     this.mqLamp10.setPreferredSize(new Dimension(26, 12));
/* 170:166 */     this.mqLamp10.setState(false);
/* 171:167 */     this.mqLamp10.setText("");
/* 172:168 */     add(this.mqLamp10, new GridBagConstraints());
/* 173:    */     
/* 174:170 */     this.mqLamp11.setBackground(new Color(193, 132, 28));
/* 175:171 */     this.mqLamp11.setDoubleBuffered(true);
/* 176:172 */     this.mqLamp11.setEdge(1);
/* 177:173 */     this.mqLamp11.setFont(new Font("Dialog", 1, 8));
/* 178:174 */     this.mqLamp11.setMaximumSize(new Dimension(26, 12));
/* 179:175 */     this.mqLamp11.setMinimumSize(new Dimension(13, 6));
/* 180:176 */     this.mqLamp11.setPreferredSize(new Dimension(26, 12));
/* 181:177 */     this.mqLamp11.setState(false);
/* 182:178 */     this.mqLamp11.setText("");
/* 183:179 */     add(this.mqLamp11, new GridBagConstraints());
/* 184:    */   }
/* 185:    */   
/* 186:    */   public void setState(int state)
/* 187:    */   {
/* 188:187 */     if (this.state != state)
/* 189:    */     {
/* 190:188 */       this.state = state;
/* 191:189 */       this.mqLamp0.setState(state & 0x800);
/* 192:190 */       this.mqLamp1.setState(state & 0x400);
/* 193:191 */       this.mqLamp2.setState(state & 0x200);
/* 194:192 */       this.mqLamp3.setState(state & 0x100);
/* 195:193 */       this.mqLamp4.setState(state & 0x80);
/* 196:194 */       this.mqLamp5.setState(state & 0x40);
/* 197:195 */       this.mqLamp6.setState(state & 0x20);
/* 198:196 */       this.mqLamp7.setState(state & 0x10);
/* 199:197 */       this.mqLamp8.setState(state & 0x8);
/* 200:198 */       this.mqLamp9.setState(state & 0x4);
/* 201:199 */       this.mqLamp10.setState(state & 0x2);
/* 202:200 */       this.mqLamp11.setState(state & 0x1);
/* 203:    */     }
/* 204:    */   }
/* 205:    */   
/* 206:224 */   private int state = -1;
/* 207:    */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Panel.MqLamps
 * JD-Core Version:    0.7.0.1
 */