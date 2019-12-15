vidobj = VideoReader('test1.mp4'); %53 frames
numFrames = get(vidobj,'NumberOfFrames');
%%
SE = strel('disk',10);
centroids = zeros(18,2);
final_im = zeros(720,1131);
final_im = logical(final_im);
for i = 26:43
    frame = rgb2gray(im2double(read(vidobj,i))); %read in each frame
    frame = frame(:,75:1205,:);
    frame = imsharpen(frame);
    bw = im2bw(frame,0.95);
    frame_new = imerode(bw,SE);
    frame_new = imdilate(bw,SE);
    frame_new(1:422,:) = 0;
    
    final_im = final_im | frame_new;
    %figure,imshow(frame_new)
    Ilabel = bwlabel(frame_new);
    stat = regionprops(Ilabel,'centroid');
    centroids(i-23,:) = stat.Centroid(1:2);
end
figure,imshow(final_im)
imwrite(final_im,'path1.jpg');