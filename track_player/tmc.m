function [result,posi, posj]=tmc(image1,image2)

if size(image1,3)==3
    image1=rgb2gray(image1);
end
if size(image2,3)==3
    image2=rgb2gray(image2);
end

if size(image1)>size(image2)
    Target=image1;
    Template=image2;
else
    Target=image2;
    Template=image1;
end

[r1,c1]=size(Target);
[r2,c2]=size(Template);

image22=Template-mean(mean(Template));

corrMat=[];
mse= 100e1000;
for i=1:(r1-r2+1) 
    for j=1:(c1-c2+1)
        Nimage=Target(i:i+r2-1,j:j+c2-1);
        Nimage=Nimage-mean(mean(Nimage));
        if (sumabs((Nimage-image22))<mse)
            mse= sumabs((Nimage-image22));
            posi=i;
            posj=j;
        %corr=sum(sum(Nimage.*image22));
        %corrMat(i,j)=corr;
    end
  end
end
result=zeros(size(Target));
for i=-10 :10
    for j=-10:10
       result(i+posi,j+posj)=255;
    end
end



