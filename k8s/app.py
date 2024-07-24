from flask import Flask, request
import time
import math

app = Flask(__name__)

@app.route('/')
def index():
    return "Hello, World!"

@app.route('/sleep')
def sleep():
    duration = int(request.args.get('duration', 5))
    time.sleep(duration)
    return f"Slept for {duration} seconds"

@app.route('/cpu')
def cpu_load():
    duration = int(request.args.get('duration', 5))
    start_time = time.time()
    # Perform a CPU-intensive task
    while time.time() - start_time < duration:
        _ = math.sqrt(123456789)  # CPU-intensive calculation
    return f"Completed CPU load for {duration} seconds"

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)
