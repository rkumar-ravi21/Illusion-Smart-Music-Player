import send_file
import os
from flask import Flask, request, send_from_directory
from werkzeug import secure_filename
import create_file


#Creating Flask App
app = Flask(__name__)
app.config['UPLOAD_FOLDER'] = "//home//ravi//PycharmProjects//Send_Music"
app.config['MUSIC_FOLDER'] = "//home//ravi//PycharmProjects//Music_Tempo//Database"
file_names = []
file_number = 0
len_of_files = 0


#Receiving Data
@app.route("/")
@app.route("/first_song", methods = ['POST', 'GET'])
def first_song():
    global file_names, file_number, len_of_files
    if(request.method == 'POST'):
        #f = request.files['file1']
        #f.save(os.path.join(app.config['UPLOAD_FOLDER'], secure_filename(f.filename)))
        #f = request.files['file2']
        #f.save(os.path.join(app.config['UPLOAD_FOLDER'], secure_filename(f.filename)))
        data = request.get_json()
        create_file.create.create_csv(data)
        return "0"
        #file_names = send_file.send_files.run()
        #len_of_files = len(file_names)
        #mn = file_names[file_number]
        #mn = mn.rpartition('/')[-1]
        #send_from_directory(app.config['MUSIC_FOLDER'], filename = mn, as_attachment = True)   #open file and return binary data-------------------------------------------
        #return "File Send"
    else:
        print("Some error occurred in first song button")
        return "Some Error Occurred"



#Sending Music Files
@app.route("/next_button", methods = ['POST', 'GET'])
def next_button():
    global file_names, file_number, len_of_files
    if(request.method == 'POST'):
        file_number = file_number + 1
        if(file_number < len_of_files):
            mn = file_names[file_number]
            mn = mn.rpartition('/')[-1]
            send_from_directory(app.config['MUSIC_FOLDER'], filename=mn, as_attachment=True)  #open file and return binary data----------------------------------------
            print("File send via next button")
            return "Files Send"
        else:
            print("Song list finished")
            return "Song list finished"
    else:
        print("Some error occurred in next button")
        return "Some Error Occurred"



#file_names = send_file.send_files.run()
#print(file_names)
app.run(host='0.0.0.0', debug = True)
