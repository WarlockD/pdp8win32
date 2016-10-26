make 
#../../utils/showbin/showbin MAINDEC-08-D1GB-D.bin >a
#../../utils/showbin/showbin ../bin/MAINDEX-08-D1GB-PB.pt.fixed > b
#diff -u a b

../../utils/showbin/showbin MAINDEC-8I-D01C-D.bin >a
../../utils/showbin/showbin ../bin/MAINDEC-8I-D01C-PB.pt.fixed > b
diff -u a b
