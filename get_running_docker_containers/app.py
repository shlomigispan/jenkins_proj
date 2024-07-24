# app.py
from flask import Flask, jsonify
import docker

app = Flask(__name__)
client = docker.from_env()


@app.route('/containers', methods=['GET'])
def get_containers():
    containers = client.containers.list()
    container_info = []

    for container in containers:
        info = {
            "id": container.id,
            "name": container.name,
            "status": container.status,
            "image": container.image.tags,
        }
        container_info.append(info)

    response = jsonify(container_info)
    return response


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
