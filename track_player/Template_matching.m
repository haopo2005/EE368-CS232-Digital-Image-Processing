clc;clear all
vidobj = VideoReader('test_2person.mp4'); 
numFrames = get(vidobj,'NumberOfFrames');
frame = rgb2gray(im2double(read(vidobj,1)));
template= rgb2gray(im2double(imread('template copy.png')));
[h,w]=size(frame);
seq=zeros(720,1280,25);
result=zeros(720,1280,25);
output= cell(25,1)
for i=25:50
    seq(:,:,i)=adapthisteq(rgb2gray(im2double(read(vidobj,i))),'NumTiles',[16,16]);
    result(:,:,i)=tmc(seq(:,:,i),template);
    output{i-24}= im2double(result(:,:,i));
end
%%
vidwriter = VideoWriter('2person_temp_path.mp4','MPEG-4');
vidwriter1 = VideoWriter('2person_seq_path.mp4','MPEG-4');
vidwriter.FrameRate = vidobj.FrameRate;
open(vidwriter);
open(vidwriter1);
for i = 1:25
    writeVideo(vidwriter,mat2gray(output{i}));
    writeVideo(vidwriter1,mat2gray(seq(:,:,(i+24))));
end
close(vidwriter);
close(vidwriter1);