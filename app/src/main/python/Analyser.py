import io
import numpy as np
import matplotlib.pyplot as plt
from scipy.signal import find_peaks
import re
from indeterminatebeam import Beam, Support, PointLoadV, DistributedLoadV, PointTorque


def plot_SFD(ax, x_data, y_data):
    y_data = np.array(y_data) / 1000  # Convert Newtons to kiloNewtons
    y_data *= -1
    peaks, _ = find_peaks(y_data)
    troughs, _ = find_peaks(-y_data)

    peaks = np.array(peaks)
    troughs = np.array(troughs)

    ax.plot(x_data, y_data, color='blue', label='SFD Curve')
    ax.fill_between(x_data, 0, y_data, alpha=0.3, color='blue')

    # Scatter plot for peaks in red
    ax.scatter(x_data[peaks], y_data[peaks], color='red')
    for peak_x, peak_y in zip(x_data[peaks], y_data[peaks]):
        ax.annotate(f'({peak_y:.2f})', xy=(peak_x, peak_y), xytext=(peak_x - 0.1, peak_y + 0.1))

    # Scatter plot for troughs in green
    ax.scatter(x_data[troughs], y_data[troughs], color='green')
    for trough_x, trough_y in zip(x_data[troughs], y_data[troughs]):
        ax.annotate(f'({trough_y:.2f})', xy=(trough_x, trough_y),
                    xytext=(trough_x + 0.1, trough_y - 0.2))

        # Annotate y-values at the start and end of the beam
    ax.annotate(f'({y_data[0]:.2f})', xy=(x_data[0], y_data[0]),
                xytext=(x_data[0] - 0.5, y_data[0]))
    ax.annotate(f'({y_data[-1]:.2f})', xy=(x_data[-1], y_data[-1]),
                xytext=(x_data[-1], y_data[-1] + 0.1))

    ax.set_title('Shear Force Diagram')
    ax.set_xlabel('X-axis (m)')
    ax.set_ylabel('Shear Force (kN)')
    ax.set_ylim(min(y_data) - 1, max(y_data) + 1)
    ax.set_xlim(min(x_data) - 0.5, max(x_data) + 0.5)
    ax.axhline(0, color='black', linewidth=1.0)
    ax.legend()


def plot_BMD(ax, x_data, y_data):
    y_data = np.array(y_data) / 1000  # Convert Newtons to kiloNewtons
    peaks, _ = find_peaks(y_data)
    troughs, _ = find_peaks(-y_data)

    ax.plot(x_data, y_data, color='red', label='BMD Curve')
    ax.fill_between(x_data, 0, y_data, alpha=0.3, color='red')

    # Scatter plot for peaks in red
    ax.scatter(x_data[peaks], y_data[peaks], color='red')
    for peak_x, peak_y in zip(x_data[peaks], y_data[peaks]):
        ax.annotate(f'({peak_y:.2f})', xy=(peak_x, peak_y), xytext=(peak_x - 0.1, peak_y + 0.1))

    # Scatter plot for troughs in green
    ax.scatter(x_data[troughs], y_data[troughs], color='green')
    for trough_x, trough_y in zip(x_data[troughs], y_data[troughs]):
        ax.annotate(f'({trough_y:.2f})', xy=(trough_x, trough_y),
                    xytext=(trough_x + 0.1, trough_y - 0.1))

    ax.set_title('Bending Moment Diagram')
    ax.set_xlabel('X-axis (m)')
    ax.set_ylim(min(y_data) - 1, max(y_data) + 1)
    ax.set_ylabel('Bending Moment (kN.m)')
    ax.axhline(0, color='black', linewidth=1.0)
    ax.legend()


def analyse(beam_length, supports, loads):
    # Create a beam with the specified length
    beam = Beam(beam_length)

    for support in supports:
        # Extract position and support type and instantiate Support accordingly
        support_str = str(support)
        position_str = support_str.split('position=')[1].split(',')[0]
        position = float(position_str)
        support_type_str = support_str.split('supportType=')[1].strip('()').split(',')
        support_type = tuple(map(int, support_type_str))
        beam.add_supports(Support(position, support_type))

    print("Load array:", loads)
    for load in loads:
        load_str = str(load)

        if "PointLoadV" in load_str:
            magnitude_str = load_str.split('magnitude=')[1].split(',')[0]
            magnitude = float(magnitude_str)
            position_str = load_str.split('position=')[1].split(')')[0]
            position = float(position_str)
            beam.add_loads(PointLoadV(magnitude, position))
        if "PointTorque" in load_str:
            magnitude_str = load_str.split('magnitude=')[1].split(',')[0]
            magnitude = float(magnitude_str)
            position_str = load_str.split('position=')[1].split(')')[0]
            position = float(position_str)
            beam.add_loads(PointTorque(magnitude, position))

        if "DistributedLoadV" in load_str:
            magnitude_str = load_str.split('magnitude=')[1].split(',')[0]
            print("Magnitude string:", magnitude_str)
            magnitude = float(magnitude_str)
            print("Magnitude:", magnitude)
            position_range_str = load_str.split('positionRange=')[1].strip('[]()').split(',')
            print("Position range string:", position_range_str)
            # Use regular expression to extract numerical values
            position_range = tuple(float(re.search(r'\d+\.\d+', pos).group()) for pos in position_range_str)
            print("Position_range:",  position_range)
            load = DistributedLoadV(magnitude, position_range)
            beam.add_loads(load)


    # Analyze the beam
    beam.analyse()

    # Extract internal forces from the solved beam
    x_values = np.linspace(0, beam._x1, num=1000)
    shear_values = [beam.get_shear_force(x) for x in x_values]
    moment_values = [beam.get_bending_moment(x) for x in x_values]

    # Plotting shear force diagram and bending moment diagram in subplots
    fig, (ax1, ax2) = plt.subplots(2, 1, figsize=(8, 10))

    plot_SFD(ax1, x_values, shear_values)
    plot_BMD(ax2, x_values, moment_values)

    plt.tight_layout()

    # Save the plot to a byte stream
    f = io.BytesIO()
    plt.savefig(f, format="png")
    plt.close()  # Close the plot to free memory
    return f.getvalue()
