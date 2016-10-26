
  int cinf,coutf;
  int ibus, inth, intf;
  int ibf[8], pbf[8], pbp, dskrg,dskmem,dskad,dskfl,tm,hl;
  int mem[4100];
  FILE *fp;
  extern int qopen();
  extern int qread();
  extern int qwrite();
  extern int qputch();

  main()
  {
  int stl, acc;
  unsigned int i;
  char ch, cb[20];
  /* 4kMon DF32 boot */
  int ldqr[5];

  ldqr[0]=06603;ldqr[1]=06622;ldqr[2]=05201;ldqr[3]=05604;ldqr[4]=07600;
  printf( "PDP8 Emulator\n" );
  qopen("df32.dsk");
  while ( 1 ) {
  printf( "->" ); gets( cb );
  sscanf( cb, "%c %o,%o", &ch, &stl, &tm );
  switch ( ch ) {
  case 'R':
   prun( &stl, &acc );
   printf( "Halt:%04o %04o\n", stl, acc );
   pbp=( pbp-8 )&7;
   for ( i=0; i<8; i++, pbp=( 1+pbp )&7 )
    printf( "Loc:%07o %04o\n", pbf[pbp], ibf[pbp] );
   break;
  case 'L':
   loadfl( );
   break;
  case 'N':
   stl++;
  case 'E':
   printf( "Loc:%o = %o\n", stl, mem[stl] );
   break;
  case 'D':
   mem[stl]=tm;
   break;
  case 'B':
   for (tm=0;tm<4096;tm++) mem[tm]=07402;
   for ( i=0; i<sizeof( ldqr )/sizeof( int ); i++ ) {
    mem[i+0200]=ldqr[i];
   }
   mem[07750]=07576;
   mem[07751]=07576;
   break;
    }
   }
   return(0);
  }

  int SetMa(md,ma)
  int md,ma;
  {
   if ((md&07000)<06000)
    if ( md&0400 ) {
     if ( ( ma&07770 )==010 ) mem[ma]++;
     mem[ma]&=07777;
     return(mem[ma]);
    }
    return(ma);
  }

  prun( xpc, xac )
  int *xpc,*xac;
  {
  int pc, ac, ma, md, mq;
  int tcnt, flg, ilag, cch;

  pc=*xpc;
  ac=md=tcnt=mq=pbp=ilag=0;
  ibus=intf=inth=cinf=coutf=0;
  dskrg=dskfl=0;
  while ( 1 ) {
   if ( ++ilag==100 ) {
    if ( intf&&ibus ) {
     mem[0]=pc&07777;
     pc=1;
     intf=inth=0;
     }
     if ( (cch=kbinr())!=-1 ) {
      cinf=cch;
      cinf=cinf|0x80;
      if ( cinf==128+1 ) {*xpc=pc; *xac=ac; return; }
      if ( cinf==128+8 ) cinf=0377;
      if ( cinf==128+24 ) cinf=131; /* ^X->^C */
     }
    ilag=0;
    }
/*   printf( "%04o %05o\n", pc, mem[pc]  ); */
   pbf[pbp]=pc;
  ibf[pbp]=mem[pc];
  pbp=( pbp+1 )&7;
   md=mem[pc];
   ma=( ( md&0177 )+( md&0200?( pc&07600 ):0 ) );
   pc=( ( pc+1 )&07777 );
   ac&=017777;
   ibus=( cinf||coutf||dskfl );
   ma=SetMa(md,ma);
   switch ( md&07000 ) {
   case 0000:ac&=( mem[ma]|010000 );
     break;
   case 01000:ac+=mem[ma];
    break;
   case 02000:if ( ( mem[ma]=( 1+mem[ma] )&07777 )==0 ) pc++;
    break;
   case 03000:mem[ma]=ac&07777;
              ac&=010000;
    break;
   case 04000:mem[ma]=pc&07777;
              pc=( ma+1 )&07777;
    break;
   case 05000:pc=( ma&07777 );
    break;
   case 06000: flg=0;
     ac=iot( ac, md, &flg )|( ac&010000 );
     if ( flg ) pc++;
    break;
   case 07000: if ( md&0400 ) {
     if ( md&1 ) {group3( &ac, &mq, md ); break; }
      if ( md&2 ) {*xpc=pc; *xac=ac; return; }
      pc=group2( ac, pc, md );
      if ( md&0200 ) ac&=010000;
      if ( md&4 ) ac|=04002;
      break;
      }
        ac=group1( ac, md );
        break;
    }
   if ( md!=06001 ) intf=inth;
   }
  }

  int iot(acc,xmd,flg )
  int acc,xmd,*flg;
  {
  int i,*p;

  switch( xmd&0770 ) {
  case 030:
  case 040:
  switch( xmd&0777 ) {
  case 030:  cinf=0;
      break;
  case 031:  if ( cinf ) *flg=1;
      break;
  case 032:       cinf=0; return( 0 );
  case 034:  return( acc|cinf );
  case 036:  i=cinf; cinf=0; return( i );
  case 040:  coutf=1; break;
  case 041:  *flg=coutf; break;
  case 042:  coutf=0;break;
  case 046:  coutf=1;
  case 044:  coutf=1;qputch(acc&0177); break;
      }
      break;
  case 0:
    switch( xmd&0777 ) {
   case 000:  *flg=intf;
      break;
   case 001:  inth=1;
      break;
   case 002:  inth=intf=0;
      break;
   case 003:  if ( ibus ) *flg=1;
      break;
   case 004:  acc=( acc&010000 )>>1;
      if ( intf ) acc|=0200;
      if ( ibus ) acc|=01000;
      break;
   case 005:  intf=acc&0200;
      if ( acc&04000 ) acc|=010000;
      inth=1;
      break;
        }
    break;
 case 0600:
 case 0610:
 case 0620:
                switch(xmd&0777) {
        case 0601:      dskad=dskfl=0;
                        break;
        case 0605:
        case 0603:
                        i=(dskrg&070)<<9;
                        dskmem=(mem[07751]+1+i)&07777;  /* mem */
                        tm=(dskrg&03700)<<6;
                        dskad=(acc&07777)+tm;           /* dsk */
                        i=010000-mem[07750];
                        p=&mem[dskmem];
                         /* printf("D:%d>%o,",dskad/129,dskmem); */
                        if (xmd&2)              /*read */
                          qread(dskad,i,p);
                        else
                          qwrite(dskad,i,p);
                        dskfl=1;
                        mem[07751]=0;
                        mem[07750]=0;
                        acc=acc&010000;
                        break;
        case 0611:      dskrg=0;
                        break;
        case 0615:      dskrg=(acc&07777);              /* register */
                        break;
        case 0616:      acc=(acc&010000)|dskrg;
                        break;
        case 0626:      acc=(acc&010000)+(dskad&07777);
                        break;
        case 0622:      if (dskfl) *flg=1;
                        break;
        case 0612:      acc=acc&010000;
        case 0621:      *flg=1; /* No error */
                        break;
        }
 break;
 default:  break;
  }
  return( acc );
  }

  group3( acc, mmq, xmd )
  int *acc,*mmq,xmd;
  {
    int qtm;

       if ( xmd==07521 ) {qtm=*acc; *acc=*mmq|( *acc&010000 ); *mmq=qtm&07777; r
eturn; }
    if ( xmd&0200 ) *acc=*acc&010000;
    if ( xmd&020 ) {*mmq=*acc&07777; *acc&=010000; }
    if ( xmd&0100 ) *acc|=*mmq;
  }

  int group2( acc, xpc, xmd )
  int acc,xpc,xmd;
  {
  int state;

  state=0;
  if ( acc&04000 ) state|=0100;
  if ( ( acc&07777 )==0 ) state|=040;
  if ( acc&010000 ) state|=020;
  if ( ( xmd&0160 )==0 ) state=0;
  if ( xmd&010 ) {
   if ( ( ~state&xmd )==xmd ) return( xpc+1 );
    else return( xpc );
   }
   if ( state&xmd ) return( xpc+1 );
    else return( xpc );
  }

  int group1( xacc, xmd )
  int xacc,xmd;
  {
  int acc, md;

  acc=xacc; md=xmd;

  if ( md&0200 ) acc&=010000;
  if ( md&0100 ) acc&=07777;
  if ( md&040 ) acc^=07777;
  if ( md&020 ) acc^=010000;
  if ( md&1 ) acc++;
  if ( md&016 )
   switch( md&016 ) {
    case 2:  break;
    case 6:  acc=acc<<1; if ( acc&020000 ) acc++;
    case 4:  acc=acc<<1; if ( acc&020000 ) acc++;
      break;
    case 10:  if ( acc&1 ) acc|=020000; acc=acc>>1;
    case 8:  if ( acc&1 ) acc|=020000; acc=acc>>1;
      break;
   }
  return( acc&017777 );
  }

  loadfl()
  {
  int  xad, j;
  char cbx[20];

  xad=0;
  printf( "Ready:" );
  while ( 1 ) {
   gets(cbx);
   sscanf(cbx,"%o",&j);
   if ( j==017777 ) return;
   if ( j>4096 ) xad=( j&07777 );
   else
    mem[xad++]=j;
   }
  }


.