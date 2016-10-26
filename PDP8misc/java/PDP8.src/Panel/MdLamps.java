/*   1:    */ package Panel;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.awt.Dimension;
/*   5:    */ import java.awt.Font;
/*   6:    */ import java.awt.GridBagConstraints;
/*   7:    */ import java.awt.GridBagLayout;
/*   8:    */ import javax.swing.JPanel;
/*   9:    */ 
/*  10:    */ public class MdLamps
/*  11:    */   extends JPanel
/*  12:    */ {
/*  13:    */   private XLamp mdLamp0;
/*  14:    */   private XLamp mdLamp1;
/*  15:    */   private XLamp mdLamp10;
/*  16:    */   private XLamp mdLamp11;
/*  17:    */   private XLamp mdLamp2;
/*  18:    */   private XLamp mdLamp3;
/*  19:    */   private XLamp mdLamp4;
/*  20:    */   private XLamp mdLamp5;
/*  21:    */   private XLamp mdLamp6;
/*  22:    */   private XLamp mdLamp7;
/*  23:    */   private XLamp mdLamp8;
/*  24:    */   private XLamp mdLamp9;
/*  25:    */   
/*  26:    */   public MdLamps()
/*  27:    */   {
/*  28: 18 */     initComponents();
/*  29:    */   }
/*  30:    */   
/*  31:    */   private void initComponents()
/*  32:    */   {
/*  33: 29 */     this.mdLamp0 = new XLamp();
/*  34: 30 */     this.mdLamp1 = new XLamp();
/*  35: 31 */     this.mdLamp2 = new XLamp();
/*  36: 32 */     this.mdLamp3 = new XLamp();
/*  37: 33 */     this.mdLamp4 = new XLamp();
/*  38: 34 */     this.mdLamp5 = new XLamp();
/*  39: 35 */     this.mdLamp6 = new XLamp();
/*  40: 36 */     this.mdLamp7 = new XLamp();
/*  41: 37 */     this.mdLamp8 = new XLamp();
/*  42: 38 */     this.mdLamp9 = new XLamp();
/*  43: 39 */     this.mdLamp10 = new XLamp();
/*  44: 40 */     this.mdLamp11 = new XLamp();
/*  45:    */     
/*  46: 42 */     setFont(new Font("Dialog", 0, 8));
/*  47: 43 */     setMaximumSize(new Dimension(312, 12));
/*  48: 44 */     setMinimumSize(new Dimension(156, 6));
/*  49: 45 */     setPreferredSize(new Dimension(312, 12));
/*  50: 46 */     setLayout(new GridBagLayout());
/*  51:    */     
/*  52: 48 */     this.mdLamp0.setBackground(new Color(193, 77, 28));
/*  53: 49 */     this.mdLamp0.setForeground(new Color(0, 0, 0));
/*  54: 50 */     this.mdLamp0.setDoubleBuffered(true);
/*  55: 51 */     this.mdLamp0.setEdge(1);
/*  56: 52 */     this.mdLamp0.setFont(new Font("Dialog", 1, 8));
/*  57: 53 */     this.mdLamp0.setMaximumSize(new Dimension(26, 12));
/*  58: 54 */     this.mdLamp0.setMinimumSize(new Dimension(13, 6));
/*  59: 55 */     this.mdLamp0.setPreferredSize(new Dimension(26, 12));
/*  60: 56 */     this.mdLamp0.setState(false);
/*  61: 57 */     this.mdLamp0.setText("");
/*  62: 58 */     add(this.mdLamp0, new GridBagConstraints());
/*  63:    */     
/*  64: 60 */     this.mdLamp1.setBackground(new Color(193, 77, 28));
/*  65: 61 */     this.mdLamp1.setDoubleBuffered(true);
/*  66: 62 */     this.mdLamp1.setEdge(1);
/*  67: 63 */     this.mdLamp1.setFont(new Font("Dialog", 1, 8));
/*  68: 64 */     this.mdLamp1.setMaximumSize(new Dimension(26, 12));
/*  69: 65 */     this.mdLamp1.setMinimumSize(new Dimension(13, 6));
/*  70: 66 */     this.mdLamp1.setPreferredSize(new Dimension(26, 12));
/*  71: 67 */     this.mdLamp1.setState(false);
/*  72: 68 */     this.mdLamp1.setText("");
/*  73: 69 */     add(this.mdLamp1, new GridBagConstraints());
/*  74:    */     
/*  75: 71 */     this.mdLamp2.setBackground(new Color(193, 77, 29));
/*  76: 72 */     this.mdLamp2.setDoubleBuffered(true);
/*  77: 73 */     this.mdLamp2.setEdge(1);
/*  78: 74 */     this.mdLamp2.setFont(new Font("Dialog", 1, 8));
/*  79: 75 */     this.mdLamp2.setMaximumSize(new Dimension(26, 12));
/*  80: 76 */     this.mdLamp2.setMinimumSize(new Dimension(13, 6));
/*  81: 77 */     this.mdLamp2.setPreferredSize(new Dimension(26, 12));
/*  82: 78 */     this.mdLamp2.setState(false);
/*  83: 79 */     this.mdLamp2.setText("");
/*  84: 80 */     add(this.mdLamp2, new GridBagConstraints());
/*  85:    */     
/*  86: 82 */     this.mdLamp3.setBackground(new Color(193, 132, 28));
/*  87: 83 */     this.mdLamp3.setDoubleBuffered(true);
/*  88: 84 */     this.mdLamp3.setEdge(1);
/*  89: 85 */     this.mdLamp3.setFont(new Font("Dialog", 1, 8));
/*  90: 86 */     this.mdLamp3.setMaximumSize(new Dimension(26, 12));
/*  91: 87 */     this.mdLamp3.setMinimumSize(new Dimension(13, 6));
/*  92: 88 */     this.mdLamp3.setPreferredSize(new Dimension(26, 12));
/*  93: 89 */     this.mdLamp3.setState(false);
/*  94: 90 */     this.mdLamp3.setText("");
/*  95: 91 */     add(this.mdLamp3, new GridBagConstraints());
/*  96:    */     
/*  97: 93 */     this.mdLamp4.setBackground(new Color(193, 132, 28));
/*  98: 94 */     this.mdLamp4.setDoubleBuffered(true);
/*  99: 95 */     this.mdLamp4.setEdge(1);
/* 100: 96 */     this.mdLamp4.setFont(new Font("Dialog", 1, 8));
/* 101: 97 */     this.mdLamp4.setMaximumSize(new Dimension(26, 12));
/* 102: 98 */     this.mdLamp4.setMinimumSize(new Dimension(13, 6));
/* 103: 99 */     this.mdLamp4.setPreferredSize(new Dimension(26, 12));
/* 104:100 */     this.mdLamp4.setState(false);
/* 105:101 */     this.mdLamp4.setText("");
/* 106:102 */     add(this.mdLamp4, new GridBagConstraints());
/* 107:    */     
/* 108:104 */     this.mdLamp5.setBackground(new Color(193, 132, 28));
/* 109:105 */     this.mdLamp5.setDoubleBuffered(true);
/* 110:106 */     this.mdLamp5.setEdge(1);
/* 111:107 */     this.mdLamp5.setFont(new Font("Dialog", 1, 8));
/* 112:108 */     this.mdLamp5.setMaximumSize(new Dimension(26, 12));
/* 113:109 */     this.mdLamp5.setMinimumSize(new Dimension(13, 6));
/* 114:110 */     this.mdLamp5.setPreferredSize(new Dimension(26, 12));
/* 115:111 */     this.mdLamp5.setState(false);
/* 116:112 */     this.mdLamp5.setText("");
/* 117:113 */     add(this.mdLamp5, new GridBagConstraints());
/* 118:    */     
/* 119:115 */     this.mdLamp6.setBackground(new Color(193, 77, 28));
/* 120:116 */     this.mdLamp6.setDoubleBuffered(true);
/* 121:117 */     this.mdLamp6.setEdge(1);
/* 122:118 */     this.mdLamp6.setFont(new Font("Dialog", 1, 8));
/* 123:119 */     this.mdLamp6.setMaximumSize(new Dimension(26, 12));
/* 124:120 */     this.mdLamp6.setMinimumSize(new Dimension(13, 6));
/* 125:121 */     this.mdLamp6.setPreferredSize(new Dimension(26, 12));
/* 126:122 */     this.mdLamp6.setState(false);
/* 127:123 */     this.mdLamp6.setText("");
/* 128:124 */     add(this.mdLamp6, new GridBagConstraints());
/* 129:    */     
/* 130:126 */     this.mdLamp7.setBackground(new Color(193, 77, 28));
/* 131:127 */     this.mdLamp7.setDoubleBuffered(true);
/* 132:128 */     this.mdLamp7.setEdge(1);
/* 133:129 */     this.mdLamp7.setFont(new Font("Dialog", 1, 8));
/* 134:130 */     this.mdLamp7.setMaximumSize(new Dimension(26, 12));
/* 135:131 */     this.mdLamp7.setMinimumSize(new Dimension(13, 6));
/* 136:132 */     this.mdLamp7.setPreferredSize(new Dimension(26, 12));
/* 137:133 */     this.mdLamp7.setState(false);
/* 138:134 */     this.mdLamp7.setText("");
/* 139:135 */     add(this.mdLamp7, new GridBagConstraints());
/* 140:    */     
/* 141:137 */     this.mdLamp8.setBackground(new Color(193, 77, 28));
/* 142:138 */     this.mdLamp8.setDoubleBuffered(true);
/* 143:139 */     this.mdLamp8.setEdge(1);
/* 144:140 */     this.mdLamp8.setFont(new Font("Dialog", 1, 8));
/* 145:141 */     this.mdLamp8.setMaximumSize(new Dimension(26, 12));
/* 146:142 */     this.mdLamp8.setMinimumSize(new Dimension(13, 6));
/* 147:143 */     this.mdLamp8.setPreferredSize(new Dimension(26, 12));
/* 148:144 */     this.mdLamp8.setState(false);
/* 149:145 */     this.mdLamp8.setText("");
/* 150:146 */     add(this.mdLamp8, new GridBagConstraints());
/* 151:    */     
/* 152:148 */     this.mdLamp9.setBackground(new Color(193, 132, 28));
/* 153:149 */     this.mdLamp9.setDoubleBuffered(true);
/* 154:150 */     this.mdLamp9.setEdge(1);
/* 155:151 */     this.mdLamp9.setFont(new Font("Dialog", 1, 8));
/* 156:152 */     this.mdLamp9.setMaximumSize(new Dimension(26, 12));
/* 157:153 */     this.mdLamp9.setMinimumSize(new Dimension(13, 6));
/* 158:154 */     this.mdLamp9.setPreferredSize(new Dimension(26, 12));
/* 159:155 */     this.mdLamp9.setState(false);
/* 160:156 */     this.mdLamp9.setText("");
/* 161:157 */     add(this.mdLamp9, new GridBagConstraints());
/* 162:    */     
/* 163:159 */     this.mdLamp10.setBackground(new Color(193, 132, 28));
/* 164:160 */     this.mdLamp10.setDoubleBuffered(true);
/* 165:161 */     this.mdLamp10.setEdge(1);
/* 166:162 */     this.mdLamp10.setFont(new Font("Dialog", 1, 8));
/* 167:163 */     this.mdLamp10.setMaximumSize(new Dimension(26, 12));
/* 168:164 */     this.mdLamp10.setMinimumSize(new Dimension(13, 6));
/* 169:165 */     this.mdLamp10.setPreferredSize(new Dimension(26, 12));
/* 170:166 */     this.mdLamp10.setState(false);
/* 171:167 */     this.mdLamp10.setText("");
/* 172:168 */     add(this.mdLamp10, new GridBagConstraints());
/* 173:    */     
/* 174:170 */     this.mdLamp11.setBackground(new Color(193, 132, 28));
/* 175:171 */     this.mdLamp11.setDoubleBuffered(true);
/* 176:172 */     this.mdLamp11.setEdge(1);
/* 177:173 */     this.mdLamp11.setFont(new Font("Dialog", 1, 8));
/* 178:174 */     this.mdLamp11.setMaximumSize(new Dimension(26, 12));
/* 179:175 */     this.mdLamp11.setMinimumSize(new Dimension(13, 6));
/* 180:176 */     this.mdLamp11.setPreferredSize(new Dimension(26, 12));
/* 181:177 */     this.mdLamp11.setState(false);
/* 182:178 */     this.mdLamp11.setText("");
/* 183:179 */     add(this.mdLamp11, new GridBagConstraints());
/* 184:    */   }
/* 185:    */   
/* 186:    */   public void setState(int state)
/* 187:    */   {
/* 188:187 */     if (this.state != state)
/* 189:    */     {
/* 190:188 */       this.state = state;
/* 191:189 */       this.mdLamp0.setState(state & 0x800);
/* 192:190 */       this.mdLamp1.setState(state & 0x400);
/* 193:191 */       this.mdLamp2.setState(state & 0x200);
/* 194:192 */       this.mdLamp3.setState(state & 0x100);
/* 195:193 */       this.mdLamp4.setState(state & 0x80);
/* 196:194 */       this.mdLamp5.setState(state & 0x40);
/* 197:195 */       this.mdLamp6.setState(state & 0x20);
/* 198:196 */       this.mdLamp7.setState(state & 0x10);
/* 199:197 */       this.mdLamp8.setState(state & 0x8);
/* 200:198 */       this.mdLamp9.setState(state & 0x4);
/* 201:199 */       this.mdLamp10.setState(state & 0x2);
/* 202:200 */       this.mdLamp11.setState(state & 0x1);
/* 203:    */     }
/* 204:    */   }
/* 205:    */   
/* 206:224 */   private int state = -1;
/* 207:    */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Panel.MdLamps
 * JD-Core Version:    0.7.0.1
 */