function varargout = Im_Text_v1(varargin)
% IM_TEXT_V1 MATLAB code for Im_Text_v1.fig
%      IM_TEXT_V1, by itself, creates a new IM_TEXT_V1 or raises the existing
%      singleton*.
%
%      H = IM_TEXT_V1 returns the handle to a new IM_TEXT_V1 or the handle to
%      the existing singleton*.
%
%      IM_TEXT_V1('CALLBACK',hObject,eventData,handles,...) calls the local
%      function named CALLBACK in IM_TEXT_V1.M with the given input arguments.
%
%      IM_TEXT_V1('Property','Value',...) creates a new IM_TEXT_V1 or raises the
%      existing singleton*.  Starting from the left, property value pairs are
%      applied to the GUI before Im_Text_v1_OpeningFcn gets called.  An
%      unrecognized property name or invalid value makes property application
%      stop.  All inputs are passed to Im_Text_v1_OpeningFcn via varargin.
%
%      *See GUI Options on GUIDE's Tools menu.  Choose "GUI allows only one
%      instance to run (singleton)".
%
% See also: GUIDE, GUIDATA, GUIHANDLES

% Edit the above text to modify the response to help Im_Text_v1

% Last Modified by GUIDE v2.5 07-Dec-2016 23:33:59

% Begin initialization code - DO NOT EDIT
gui_Singleton = 1;
gui_State = struct('gui_Name',       mfilename, ...
                   'gui_Singleton',  gui_Singleton, ...
                   'gui_OpeningFcn', @Im_Text_v1_OpeningFcn, ...
                   'gui_OutputFcn',  @Im_Text_v1_OutputFcn, ...
                   'gui_LayoutFcn',  [] , ...
                   'gui_Callback',   []);
if nargin && ischar(varargin{1})
    gui_State.gui_Callback = str2func(varargin{1});
end

if nargout
    [varargout{1:nargout}] = gui_mainfcn(gui_State, varargin{:});
else
    gui_mainfcn(gui_State, varargin{:});
end
% End initialization code - DO NOT EDIT


% --- Executes just before Im_Text_v1 is made visible.
function Im_Text_v1_OpeningFcn(hObject, eventdata, handles, varargin)
% This function has no output args, see OutputFcn.
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
% varargin   command line arguments to Im_Text_v1 (see VARARGIN)

% Choose default command line output for Im_Text_v1
handles.output = hObject;

% Update handles structure
guidata(hObject, handles);

% UIWAIT makes Im_Text_v1 wait for user response (see UIRESUME)
% uiwait(handles.figure1);


% --- Outputs from this function are returned to the command line.
function varargout = Im_Text_v1_OutputFcn(hObject, eventdata, handles) 
% varargout  cell array for returning output args (see VARARGOUT);
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Get default command line output from handles structure
varargout{1} = handles.output;


% --- Executes on button press in LoadIm_pushbutton.
function LoadIm_pushbutton_Callback(hObject, eventdata, handles)
% hObject    handle to LoadIm_pushbutton (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
handles = guidata(hObject);
[filename,pathname] = uigetfile({'*.jpg';'*.bmp'},'File Selector');
handles.image_path = strcat(pathname, filename);
handles.image = imread(handles.image_path);
axes(handles.axes1);
imshow(handles.image)
guidata(hObject, handles);

% --- Executes on button press in Analy_pushbutton.
function Analy_pushbutton_Callback(hObject, eventdata, handles)
% hObject    handle to Analy_pushbutton (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
handles = guidata(hObject);
axes(handles.axes1);
[results,search_words] = Analyze(handles);
textLabel = sprintf(results);
handles.OutputResult = search_words;
set(handles.Output_edit, 'Max', 2)
set(handles.Output_edit, 'String', textLabel);
guidata(hObject, handles);


function EnlargeFactor_Edit_Callback(hObject, eventdata, ~)
% hObject    handle to EnlargeFactor_Edit (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of EnlargeFactor_Edit as text
%        str2double(get(hObject,'String')) returns contents of EnlargeFactor_Edit as a double
handles = guidata(hObject);
handles.params.enlargeF = get(hObject,'String');
guidata(hObject, handles);

% --- Executes during object creation, after setting all properties.
function EnlargeFactor_Edit_CreateFcn(hObject, eventdata, handles)
% hObject    handle to EnlargeFactor_Edit (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function MinA_Edit_Callback(hObject, eventdata, ~)
% hObject    handle to MinA_Edit (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of MinA_Edit as text
%        str2double(get(hObject,'String')) returns contents of MinA_Edit as a double
handles = guidata(hObject);
handles.params.minA = get(hObject,'String');
guidata(hObject, handles);

% --- Executes during object creation, after setting all properties.
function MinA_Edit_CreateFcn(hObject, eventdata, handles)
% hObject    handle to MinA_Edit (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function MaxA_edit_Callback(hObject, eventdata, ~)
% hObject    handle to MaxA_edit (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of MaxA_edit as text
%        str2double(get(hObject,'String')) returns contents of MaxA_edit as a double
handles = guidata(hObject);
handles.params.maxA = get(hObject,'String');
guidata(hObject, handles);

% --- Executes during object creation, after setting all properties.
function MaxA_edit_CreateFcn(hObject, eventdata, handles)
% hObject    handle to MaxA_edit (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function MinL_edit_Callback(hObject, eventdata, handles)
% hObject    handle to MinL_edit (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of MinL_edit as text
%        str2double(get(hObject,'String')) returns contents of MinL_edit as a double
handles = guidata(hObject);
handles.params.minL = get(hObject,'String');
guidata(hObject, handles);

% --- Executes during object creation, after setting all properties.
function MinL_edit_CreateFcn(hObject, eventdata, handles)
% hObject    handle to MinL_edit (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end

function StrokeThresh_edit_Callback(hObject, eventdata, handles)
% hObject    handle to StrokeThresh_edit (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of StrokeThresh_edit as text
%        str2double(get(hObject,'String')) returns contents of StrokeThresh_edit as a double
handles = guidata(hObject);
handles.params.StrokeThreshold = get(hObject,'String');
guidata(hObject, handles);

% --- Executes during object creation, after setting all properties.
function StrokeThresh_edit_CreateFcn(hObject, eventdata, handles)
% hObject    handle to StrokeThresh_edit (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end

function Output_edit_Callback(hObject, eventdata, handles)
% hObject    handle to Output_edit (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of Output_edit as text
%        str2double(get(hObject,'String')) returns contents of Output_edit as a double


% --- Executes during object creation, after setting all properties.
function Output_edit_CreateFcn(hObject, eventdata, handles)
% hObject    handle to Output_edit (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on button press in MatlabOcr_CheckBox.
function MatlabOcr_CheckBox_Callback(hObject, eventdata, handles)
% hObject    handle to MatlabOcr_CheckBox (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hint: get(hObject,'Value') returns toggle state of MatlabOcr_CheckBox
handles = guidata(hObject);
handles.params.MatlabOcr = get(hObject,'Value');
guidata(hObject, handles);

% --- Executes on button press in TeOcr_CheckBox.
function TeOcr_CheckBox_Callback(hObject, eventdata, handles)
% hObject    handle to TeOcr_CheckBox (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hint: get(hObject,'Value') returns toggle state of TeOcr_CheckBox
handles = guidata(hObject);
handles.params.TeOcr = get(hObject,'Value');
guidata(hObject, handles);

% --- Executes on button press in Link_Pushbutton.
function Link_Pushbutton_Callback(hObject, eventdata, handles)
% hObject    handle to Link_Pushbutton (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
key_string = handles.OutputResult
web_access(key_string);


% --- Executes on button press in Preview_pushbutton.
function Preview_pushbutton_Callback(hObject, eventdata, handles)

% hObject    handle to Preview_pushbutton (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
%figure;

handles = guidata(hObject);
% choose which webcam (winvideo-1) and which  mode (YUY2_176x144)
vid = videoinput('winvideo', 1);
% only capture one frame per trigger, we are not recording a video
%vid.FramesPerTrigger = 1;
% output would image in RGB color space
vid.ReturnedColorspace = 'rgb';
% tell matlab to start the webcam on user request, not automatically
triggerconfig(vid, 'manual');
% we need this to know the image height and width
vidRes = get(vid, 'VideoResolution');
% image width
imWidth = vidRes(1);
% image height
imHeight = vidRes(2);
% number of bands of our image (should be 3 because it's RGB)
nBands = get(vid, 'NumberOfBands');
% create an empty image container and show it on axPreview
hImage = image(zeros(imHeight, imWidth, nBands), 'parent', handles.axes1);
% begin the webcam preview
preview(vid,hImage);
handles.vid = vid;
guidata(hObject, handles);


% --- Executes on button press in Snap_pushbutton.
function Snap_pushbutton_Callback(hObject, eventdata, handles)
% hObject    handle to Snap_pushbutton (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
if ~isfield(handles, 'vid')
    warndlg('Please do the preview first!');
    return;
end
vid=handles.vid;
vid.FramesPerTrigger = 1;
vid.ReturnedColorspace = 'rgb';
triggerconfig(vid, 'manual');
vidRes = get(vid, 'VideoResolution');
imWidth = vidRes(1);
imHeight = vidRes(2);
nBands = get(vid, 'NumberOfBands');
hImage = image(zeros(imHeight, imWidth, nBands), 'parent',  handles.axes1);
preview(vid, hImage);

% prepare for capturing the image preview
start(vid); 
% pause for 3 seconds to give our webcam a "warm-up" time
pause(1); 
% do capture!
trigger(vid);
% stop the preview
stoppreview(vid);
% get the captured image data and save it on capt1 variable
capt1 = getdata(vid);
handles.image = capt1;
axes(handles.axes1);
imshow(handles.image)
guidata(hObject, handles);
