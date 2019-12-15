clear, close all
vidobj = VideoReader('test_2person.mp4');
numFrames = get(vidobj,'NumberOfFrames');
frame1 = im2bw(adapthisteq(rgb2gray(read(vidobj,1)),'NumTiles',[16 16]),0.97);
disc_locations = zeros(720,1280,numFrames);
for i = 1:numFrames
    frame = rgb2gray(im2double(read(vidobj,i))); %read in each frame
    frame = adapthisteq(frame,'NumTiles',[16 16]);
    bw = im2bw(frame,0.97);
    bw_final=imerode((bw-frame1),strel('disk',1));
    disc_locations(:,:,i) = bw_final;
end

vidwriter = VideoWriter('2person_path.mp4','MPEG-4');
vidwriter.FrameRate = vidobj.FrameRate;
open(vidwriter);
for i = 1:numFrames
    writeVideo(vidwriter,max(disc_locations(:,:,i),0));
end
close(vidwriter);