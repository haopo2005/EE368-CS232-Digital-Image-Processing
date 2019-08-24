function vinfo = extract_volumeinfo(ledge,redge)
    assert(size(ledge,1) == size(redge,1) && size(ledge,2) == size(redge,2),'edges must be 1-to-1');
    assert(size(ledge,1) > 1,'edge must contain at least 2 points');
    
    vinfo = cell(5,1);
    ledge = double(ledge);
    redge = double(redge);
    % compute the volume
    vinfo{1} = sum(redge(:,2)) - sum(ledge(:,2));%the total area
    % compute center of mass
    weight = abs(redge(:,2) - ledge(:,2))/vinfo{1};
    vinfo{2} = [0,0];
    vinfo{2}(1) = sum(weight.*ledge(:,1));
    vinfo{2}(2) = sum(0.5*weight.*(ledge(:,2) + redge(:,2)));
    % CoM at different height
    vinfo{3} = abs(redge(:,2) + ledge(:,2))./2;
    % diameter at different height,distance from left to right
    vinfo{4} = abs(redge(:,2) - ledge(:,2));
    % left and right edge local gradient (pointing down is +)
    vinfo{5} = zeros(size(ledge,1),2);
    vinfo{5}(2:size(ledge,1),1) = ledge(2:size(ledge,1),2) - ledge(1:size(ledge,1)-1,2);
    vinfo{5}(2:size(redge,1),2) = -redge(2:size(redge,1),2) + redge(1:size(redge,1)-1,2);

end