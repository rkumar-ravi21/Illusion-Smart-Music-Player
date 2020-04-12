import send_files
import os
from flask import Flask, request, send_from_directory, send_file, jsonify
import werkzeug
import create_file


#Creating Flask App
app = Flask(__name__, static_folder = 'DB')
music_file_list = []
music_file_number = 0
len_of_files = 0


#Receiving Data
@app.route("/")
@app.route("/first_song", methods = ['POST', 'GET'])
def first_song():
    global music_file_list, music_file_number, len_of_files
    if(request.method == 'POST' or request.method == 'GET'):
        data = request.get_json()
        create_file.create.create_csv(data)
        music_file_list = send_files.send_music_files.run()
        len_of_files = len(music_file_list)
        server_path_name = music_file_list[music_file_number]
        server_path_name = server_path_name.rpartition('/')[-1]
        #server_path_name = "http://8f982ef9.ngrok.io/DB/" + server_path_name
        server_path_name = "http://10.42.0.1:5000/DB/" + server_path_name
        #server_path_name = "http://192.168.43.16:5000/DB/" + server_path_name
        #server_path_name = "http://127.0.0.1:5000/DB/" + server_path_name
        #print("Server Path: "+server_path_name)
        return jsonify({"path":server_path_name})
	#return redirect(filepath)
	#return send_file(fp, attachment_filename=fn, as_attachment=True)
    else:
        print("Some error occurred in first song button")
        return jsonify({"path": "error"})



#Sending Music Files
@app.route("/next_song", methods = ['POST', 'GET'])
def next_button():
    global music_file_list, music_file_number, len_of_files
    if(request.method == 'POST' or request.method == 'GET'):
        music_file_number = music_file_number + 1
        if(music_file_number < len_of_files):
            server_path_name = music_file_list[music_file_number]
            server_path_name = server_path_name.rpartition('/')[-1]
            #server_path_name = "http://8f982ef9.ngrok.io/DB/" + server_path_name
            server_path_name = "http://10.42.0.1:5000/DB/" + server_path_name
            #print("Server Path: " + server_path_name)
            return jsonify({"path": server_path_name})
        else:
            print("Song list finished")
            music_file_number = 0
            return jsonify({"path": "error"})
    else:
        print("Some error occurred in next button")
        return jsonify({"path": "error"})




#file_names = send_file.send_files.run()
#print(file_names)
app.run(host='0.0.0.0', port=5000, debug = True)
