import subprocess
import time
import matplotlib.pyplot as plt

# Values to test
particle_counts = list(range(0, 10001, 1000))
g = 0.0001
radius = 2.0

results = []

for num in particle_counts:
    print(f"\nRunning simulation with {num} particles...")

    # Start Java process
    process = subprocess.Popen(
        ['java', 'Main'],
        stdin=subprocess.PIPE,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True,
        bufsize=1
    )

    # Send inputs to Java process
    process.stdin.write(f"{num}\n{g}\n{radius}\n")
    process.stdin.flush()

    frame_count = 0
    start_time = time.time()

    # Read stdout line-by-line
    while frame_count < 50:
        line = process.stdout.readline()
        if not line:
            break
        if "frame complete" in line:
            frame_count += 1

    end_time = time.time()
    elapsed = end_time - start_time
    results.append((num, elapsed))
    print(f"Completed 50 frames in {elapsed:.2f} seconds")

    # Kill the process
    process.kill()

# Plotting results
particle_vals, times = zip(*results)

plt.figure(figsize=(10, 6))
plt.plot(particle_vals, times, marker='o')
plt.title("Time for 50 frames vs Number of Particles")
plt.xlabel("Number of Particles")
plt.ylabel("Time Taken (seconds)")
plt.grid(True)
plt.savefig("brute_force.png")
plt.show()

# Optionally, save raw data
with open("performance_data.csv", "w") as f:
    f.write("Particles,Time\n")
    for n, t in results:
        f.write(f"{n},{t:.4f}\n")

