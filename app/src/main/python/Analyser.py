import io
import numpy as np
import matplotlib.pyplot as plt
from matplotlib.patches import Rectangle
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
        ax.annotate(f'({peak_y:.2f})', xy=(peak_x, peak_y), xytext=(peak_x - 0.1, peak_y + 0.1),
                    fontsize=20)

    # Scatter plot for troughs in green
    ax.scatter(x_data[troughs], y_data[troughs], color='green')
    for trough_x, trough_y in zip(x_data[troughs], y_data[troughs]):
        ax.annotate(f'({trough_y:.2f})', xy=(trough_x, trough_y),
                    xytext=(trough_x + 0.1, trough_y - 0.2), fontsize=20)

        # Annotate y-values at the start and end of the beam
    ax.annotate(f'({y_data[0]:.2f})', xy=(x_data[0], y_data[0]),
                xytext=(x_data[0] - 0.5, y_data[0]), fontsize=20)
    ax.annotate(f'({y_data[-1]:.2f})', xy=(x_data[-1], y_data[-1]),
                xytext=(x_data[-1], y_data[-1] + 0.1), fontsize=20)

    ax.set_title('Shear Force Diagram', fontsize=24, fontweight='bold')
    ax.set_xlabel('X-axis (m)', fontsize=20)
    ax.set_ylabel('Shear Force (kN)', fontsize=20)
    ax.set_ylim(min(y_data) - 1, max(y_data) + 1)
    ax.set_xlim(min(x_data) - 0.5, max(x_data) + 0.5)
    ax.axhline(0, color='black', linewidth=1.0)
    ax.legend(fontsize=24)

    # Adjust the font size and weight of ticks
    ax.tick_params(axis='both', which='major', labelsize=20)


def plot_BMD(ax, x_data, y_data):
    y_data = np.array(y_data) / 1000  # Convert Newtons to kiloNewtons
    peaks, _ = find_peaks(y_data)
    troughs, _ = find_peaks(-y_data)

    ax.plot(x_data, y_data, color='red', label='BMD Curve')
    ax.fill_between(x_data, 0, y_data, alpha=0.3, color='red')

    # Annotate y-values at the start and end of the beam if they are not zero
    if abs(y_data[0]) != 0:
        ax.annotate(f'({y_data[0]:.2f})', xy=(x_data[0], y_data[0]),
                    xytext=(x_data[0] - 0.5, y_data[0]), fontsize=20)
    if abs(y_data[-1]) != 0:
        ax.annotate(f'({y_data[-1]:.2f})', xy=(x_data[-1], y_data[-1]),
                    xytext=(x_data[-1], y_data[-1] + 0.1), fontsize=20)

    # Scatter plot for peaks in red
    ax.scatter(x_data[peaks], y_data[peaks], color='red')
    for peak_x, peak_y in zip(x_data[peaks], y_data[peaks]):
        ax.annotate(f'({peak_y:.2f})',
                    xy=(peak_x, peak_y), xytext=(peak_x - 0.1, peak_y + 0.1), fontsize=20)

    # Scatter plot for troughs in green
    ax.scatter(x_data[troughs], y_data[troughs], color='green')
    for trough_x, trough_y in zip(x_data[troughs], y_data[troughs]):
        ax.annotate(f'({trough_y:.2f})', xy=(trough_x, trough_y),
                    xytext=(trough_x + 0.1, trough_y - 0.1), fontsize=20)

    ax.set_title('Bending Moment Diagram', fontsize=24, fontweight='bold')
    ax.set_xlabel('X-axis (m)', fontsize=20)
    ax.set_ylim(min(y_data) - 1, max(y_data) + 1)
    ax.set_xlim(min(x_data) - 0.5, max(x_data) + 0.5)
    ax.set_ylabel('Bending Moment (kN.m)', fontsize=20)
    ax.axhline(0, color='black', linewidth=1.0)
    ax.legend(fontsize=20)
    # Adjust the font size and weight of ticks
    ax.tick_params(axis='both', which='major', labelsize=20)


def model(beam_length, supports, loads):
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
            position_range = tuple(
                float(re.search(r'\d+\.\d+', pos).group()) for pos in position_range_str)
            print("Position_range:", position_range)
            load = DistributedLoadV(magnitude, position_range)
            beam.add_loads(load)
    return beam


def analyse(beam_length, supports, loads):
    beam = model(beam_length, supports, loads)
    # Analyze the beam
    beam.analyse()

    # Extract internal forces from the solved beam
    x_values = np.linspace(0, beam._x1, num=1000)
    shear_values = [beam.get_shear_force(x) for x in x_values]
    moment_values = [beam.get_bending_moment(x) for x in x_values]

    # Plotting shear force diagram and bending moment diagram in subplots
    fig, (ax1, ax2) = plt.subplots(2, 1, figsize=(14, 16))
    plot_SFD(ax1, x_values, shear_values)
    plot_BMD(ax2, x_values, moment_values)

    plt.tight_layout()

    # Save the plot to a byte stream
    f = io.BytesIO()
    plt.savefig(f, format="png")
    plt.close()  # Close the plot to free memory
    return f.getvalue()


def draw_beam(beam_length, supports, loads):
    beam = model(beam_length, supports, loads)

    beam_color = 'lightgray'
    color = 'blue'
    triangle_height = 10
    segment_space = 1
    scale_factor = 20
    udl_length = 7

    beam_length = beam._x1
    x_start = 0
    fig, ax = plt.subplots(figsize=(8, 6))  # Adjusted figsize here
    # Draw vertical segments
    for i in range(int(beam_length / segment_space) + 1):
        x_position = x_start + i * segment_space
        ax.axvline(x_position, color='lightgray', linestyle='dotted', linewidth=0.8)

    # # Draw beam
    beam_rect = Rectangle((x_start, 90 - triangle_height / 2), beam_length - x_start, 10,
                          color=beam_color)
    ax.add_patch(beam_rect)

    for support in beam._supports:
        # print("support:", support)
        x = support._position
        y = 81
        if support._fixed[1] == 1 and support._fixed[0] == 0:  # Roller support
            ax.plot(x, y, 'o', color=color, markersize=8)
        elif support._fixed[0] == 1 and support._fixed[1] == 1 and support._fixed[
            2] != 1:  # Pinned support
            ax.plot(x, y, '^', color=color, markersize=8)
        elif support._fixed[2] == 1:  # Fixed support
            ax.vlines(x, 70, 110, color='black', linewidth=4)

    # Draw loads
    for load in beam._loads:
        if isinstance(load, PointLoadV):
            ax.arrow(load.position, 97 + scale_factor, 0, -scale_factor, head_width=0.2,
                     head_length=3, fc='red', ec='red')
        elif isinstance(load, DistributedLoadV):
            # Access the span attribute from the load instance
            load_range = load.span
            x_start = load_range[0]
            x_end = load_range[1]
            x_arrows = np.arange(x_start, x_end + 0.0001, 0.5)
            for arrows in x_arrows:
                ax.arrow(arrows, 97 + udl_length, 0, -udl_length, head_width=0.1, head_length=2,
                         fc='blue', ec='blue')
        elif isinstance(load, PointTorque):
            ax.arrow(load.position, 97 + scale_factor, 0, -scale_factor, head_width=0.2,
                     head_length=3, fc='green', ec='green')

    ax.set_xlim([-beam_length * 0.2, beam_length + beam_length * 0.2])
    ax.set_ylim([0, 200])
    ax.get_yaxis().set_visible(False)

    plt.tight_layout()

    # Save the plot to a byte stream
    f = io.BytesIO()
    plt.savefig(f, format="png")
    plt.close()  # Close the plot to free memory
    return f.getvalue()


def getSupportReaction(beam_length, supports, loads):
    beam = model(beam_length, supports, loads)
    beam.analyse()
    reactions = []

    for support in supports:
        support_str = str(support)
        position_str = support_str.split('position=')[1].split(',')[0]
        position = float(position_str)
        reaction = beam.get_reaction(position)
        reactions.append((position, round(reaction[1] / 1000, 2)))

    # Appending additional information to the return statement
    reaction_info = ", ".join([f"({pos}m, {react}KN)" for pos, react in reactions])
    return f"{reaction_info}"


def getMaximumBendingMoment(beam_length, supports, loads):
    beam = model(beam_length, supports, loads)
    beam.analyse()
    x_values = np.linspace(0, beam._x1, num=1000)
    moment_values = [beam.get_bending_moment(x) for x in x_values]
    max_moment = np.max(moment_values)
    max_index = np.argmax(moment_values)
    x_at_max_moment = x_values[max_index]
    min_moment = np.min(moment_values)
    min_index = np.argmin(moment_values)
    x_at_min_moment = x_values[min_index]

    # Appending additional information to the return statement
    if min_moment == 0:
        return f"({round(x_at_max_moment, 1)}m, {round(max_moment / 1000, 2)}KNm)"
    else:
        return (f"({round(x_at_max_moment, 1)}m, {round(max_moment / 1000, 2)}KNm)",
                f"({round(x_at_min_moment, 1)}m, {round(min_moment / 1000, 2)}KNm)")
