x=100:10:200;
a=[64.55 , 48.96 ,44.84, 43.16, 41.27 ,39.66,39.12 ,34.24 , 33.75 ,28.76 ,27.23 ];
plot(x,a,'*-');
%axis([100,200,60,120])
x1 = [2 3 4 5 6  7 8 9 10 15 20];
set(gca,'XTick',[100:10:200]) 
set(gca,'xticklabel',x1); 
set(gca,'YTick',[20:10:60]) 
axis equal