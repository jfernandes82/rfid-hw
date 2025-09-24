using System.Linq;
using Windows.Devices.Enumeration;

namespace BLEDemo
{
    public class BluetoothLEInformation
    {
        public readonly DeviceInformation DeviceInformation;

        private string _macAddress;
        /// <summary>
        /// The MAC address of the Bluetooth LE device.
        /// Mac地址
        /// </summary>
        public string MacAddress
        {
            get { return _macAddress; }
            set { _macAddress = value; }
        }

        private string _name;
        /// <summary>
        /// The name of the Bluetooth LE device.
        /// 设备名称
        /// </summary>
        public string Name
        {
            get { return _name; }
            set { _name = value; }
        }

        private string _id;
        /// <summary>
        /// The unique identifier of the Bluetooth LE device.
        /// 设备ID
        /// </summary>
        public string Id
        {
            get { return _id; }
            set { _id = value; }
        }

        private bool _isPaired;
        /// <summary>
        /// Whether the Bluetooth LE device is paired.
        /// 设备是否配对
        /// </summary>
        public bool IsPaired
        {
            get { return _isPaired; }
            set { _isPaired = value; }
        }

        private bool _isCanPair;
        /// <summary>
        /// Whether the Bluetooth LE device can be paired.
        /// 设备是否可以配对
        /// </summary>
        public bool IsCanPair
        {
            get { return _isCanPair; }
            set { _isCanPair = value; }
        }

        private int _signalStrength;
        /// <summary>
        /// The signal strength of the Bluetooth LE device.
        /// 信号强度
        /// </summary>
        public int SignalStrength
        {
            get { return _signalStrength; }
            set { _signalStrength = value; }
        }

        public BluetoothLEInformation(DeviceInformation deviceInformation)
        {
            DeviceInformation = deviceInformation;
            Init();
        }

        private void Init()
        {
            MacAddress = DeviceInformation.Id.Split('-')[1];
            Name = DeviceInformation.Name;
            Id = DeviceInformation.Id;
            IsPaired = DeviceInformation.Pairing.IsPaired;
            IsCanPair = DeviceInformation.Pairing.CanPair;
            if (DeviceInformation.Properties.ContainsKey("System.Devices.Aep.SignalStrength"))
            {
                var Signal = DeviceInformation.Properties.Single(d => d.Key == "System.Devices.Aep.SignalStrength").Value;
                if (Signal != null)
                {
                    SignalStrength = int.Parse(Signal.ToString());
                }
            }
        }

        public void Update(DeviceInformationUpdate deviceInformationUpdate)
        {
            DeviceInformation.Update(deviceInformationUpdate);
            Init();
        }
    }
}
