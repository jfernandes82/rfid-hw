using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Data;
using System.Diagnostics.Eventing.Reader;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Windows.Forms;
using Windows.Devices.Bluetooth;
using Windows.Devices.Bluetooth.GenericAttributeProfile;
using Windows.Devices.Enumeration;
using Windows.Security.Cryptography;
using Windows.Storage.Streams;

namespace BLEDemo
{
    public partial class FrmMain : Form
    {
        private readonly DeviceWatcher DeviceWatcher;
        BluetoothLEDevice _currentDevice;

        ObservableCollection<BluetoothLEInformation> _bleDevices = new ObservableCollection<BluetoothLEInformation>();
        ObservableCollection<ServiceInformation> _services = new ObservableCollection<ServiceInformation>();
        ObservableCollection<CharacteristicInformation> _characteristics = new ObservableCollection<CharacteristicInformation>();

        public FrmMain()
        {
            InitializeComponent();
            CheckForIllegalCrossThreadCalls = false;

            string selector = "(System.Devices.Aep.ProtocolId:=\"{bb7bb05e-5972-42b5-94fc-76eaa7084d49}\") AND (System.Devices.Aep.CanPair:=System.StructuredQueryType.Boolean#True OR System.Devices.Aep.IsPaired:=System.StructuredQueryType.Boolean#True)";
            string[] requestedProperties = { "System.Devices.Aep.IsConnected", "System.Devices.Aep.DeviceAddress", "System.Devices.Aep.Bluetooth.Le.IsConnectable", "System.Devices.Aep.SignalStrength", "System.Devices.Aep.IsPresent" };
            DeviceWatcher = DeviceInformation.CreateWatcher(selector, requestedProperties, DeviceInformationKind.AssociationEndpoint);

            DeviceWatcher.Added += OnAdded;
            DeviceWatcher.Updated += OnUpdated;
            DeviceWatcher.Removed += OnRemoved;
            DeviceWatcher.Stopped += OnStopped;
        }

        private void OnAdded(DeviceWatcher sender, DeviceInformation args)
        {
            if (string.IsNullOrEmpty(args.Name)) return;

            if (args.Properties.ContainsKey("System.Devices.Aep.SignalStrength"))
            {
                var Signal = args.Properties.Single(d => d.Key == "System.Devices.Aep.SignalStrength").Value;
                if (Signal == null) return;
                int SignalStrength = int.Parse(Signal.ToString());
                if (SignalStrength < -200) return;
                foreach (var device in _bleDevices)
                {
                    if (device.MacAddress == args.Id.Split('-')[1]) return;
                }
                _bleDevices.Add(new BluetoothLEInformation(args));
            }
        }

        private void OnUpdated(DeviceWatcher sender, DeviceInformationUpdate args)
        {
            string mac = args.Id.Split('-')[1];
            foreach (var device in _bleDevices)
            {
                if (device.MacAddress == mac)
                {
                    device.Update(args);
                    break;
                }
            }
        }

        private void OnRemoved(DeviceWatcher sender, DeviceInformationUpdate args)
        {
            string mac = args.Id.Split('-')[1];
            foreach (var device in _bleDevices)
            {
                if (device.MacAddress == mac)
                {
                    _bleDevices.Remove(device);
                    break;
                }
            }
        }

        private void OnStopped(DeviceWatcher sender, object args)
        {
        }

        private void OnConnectionStatusChanged(BluetoothLEDevice sender, object args)
        {
            if (sender.ConnectionStatus == BluetoothConnectionStatus.Disconnected)
            {
                ShowLog("Device disconnected! 设备已断开连接！", true);
                _currentDevice = null;
                btnOpen.Text = "OPEN";
                cmbDevices.Enabled = true;
                btnGetServices.Enabled = btnGetCharacteriatics.Enabled = btnGetOperation.Enabled = btnWrite.Enabled = btnRead.Enabled = btnNotify.Enabled = false;
            }
        }

        private void OnValueChanged(GattCharacteristic sender, GattValueChangedEventArgs args)
        {
            CryptographicBuffer.CopyToByteArray(args.CharacteristicValue, out byte[] data);
            if (ckbDisplayASCII1.Checked)
                rtxtReceiveBuffer.AppendText(Encoding.ASCII.GetString(data) + Environment.NewLine);
            else
                rtxtReceiveBuffer.AppendText(data.HexArray2String() + Environment.NewLine);
        }

        private void FrmMain_Load(object sender, EventArgs e)
        {

        }

        private void btnRefresh_Click(object sender, EventArgs e)
        {
            ShowLog(string.Empty);
            cmbDevices.Items.Clear();
            _bleDevices.Clear();
            LoadingHelper.ShowLoading();
            Task.Run(() =>
            {
                try
                {
                    DeviceWatcher.Start();
                    Thread.Sleep(10000);
                    DeviceWatcher.Stop();

                    foreach (var device in _bleDevices)
                    {
                        cmbDevices.Items.Add($"{device.Name}({device.Id})");
                    }
                    if (cmbDevices.Items.Count > 0)
                        cmbDevices.SelectedIndex = 0;
                }
                catch (Exception ex)
                {
                    rtxtTransmitBuffer.AppendText(ex.Message);
                }
                finally
                {
                    ShowLog("Refresh BLE devices success! 刷新BLE设备成功！");
                    LoadingHelper.CloseLoading();
                }
            });
        }

        private async void btnOpen_Click(object sender, EventArgs e)
        {
            if (cmbDevices.SelectedItem == null || cmbDevices.SelectedItem.ToString().Length == 0)
            {
                ShowLog("Please select a device first! 请先选择设备！", true);
                return;
            }
            ShowLog(string.Empty);
            LoadingHelper.ShowLoading();
            if (btnOpen.Text == "OPEN")
            {
                _currentDevice = await BluetoothLEDevice.FromIdAsync(_bleDevices[cmbDevices.SelectedIndex].Id);
                if (_currentDevice == null)
                    ShowLog("Open device failed! 打开设备失败！", true);
                else
                {
                    ShowLog("Open device success! 打开设备成功！");
                    _currentDevice.ConnectionStatusChanged += OnConnectionStatusChanged;
                    btnOpen.Text = "CLOSE";
                    btnGetServices.Enabled = true;
                    cmbDevices.Enabled = false;
                }
            }
            else
            {
                if (_currentDevice != null)
                {
                    _currentDevice.ConnectionStatusChanged -= OnConnectionStatusChanged;
                    _currentDevice.Dispose();
                    _currentDevice = null;
                    foreach (var service in _services)
                    {
                        service.GattDeviceService.Dispose();
                    }
                }
                btnOpen.Text = "OPEN";
                cmbDevices.Enabled = true;
                btnGetServices.Enabled = btnGetCharacteriatics.Enabled = btnGetOperation.Enabled = btnWrite.Enabled = btnRead.Enabled = btnNotify.Enabled = false;
                cmbServices.Items.Clear();
                cmbCharacteristics.Items.Clear();
                _services.Clear();
                _characteristics.Clear();
                ShowLog("Close device success! 关闭设备成功！");
            }
            LoadingHelper.CloseLoading();
        }

        private async void btnGetServices_Click(object sender, EventArgs e)
        {
            LoadingHelper.ShowLoading();
            ShowLog(string.Empty);
            cmbServices.Items.Clear();
            _services.Clear();
            btnGetCharacteriatics.Enabled = btnGetOperation.Enabled = btnWrite.Enabled = btnRead.Enabled = btnNotify.Enabled = false;
            GattDeviceServicesResult deviceServicesResult = await _currentDevice.GetGattServicesAsync(BluetoothCacheMode.Uncached);
            if (deviceServicesResult.Status == GattCommunicationStatus.Success)
            {
                IReadOnlyList<GattDeviceService> services = deviceServicesResult.Services;
                foreach (var service in services)
                {
                    _services.Add(new ServiceInformation(service));
                }
                foreach (var service in _services)
                {
                    cmbServices.Items.Add($"{service.Name}({service.UUID})");
                }
                if (cmbServices.Items.Count > 0)
                    cmbServices.SelectedIndex = 0;
                btnGetCharacteriatics.Enabled = true;
                ShowLog("Get services success! 获取服务成功！");
            }
            else
                ShowLog("Get services failed! 获取服务失败！", true);
            LoadingHelper.CloseLoading();
        }

        private async void btnGetCharacteriatics_Click(object sender, EventArgs e)
        {
            LoadingHelper.ShowLoading();
            ShowLog(string.Empty);
            cmbCharacteristics.Items.Clear();
            _characteristics.Clear();
            btnGetOperation.Enabled = btnWrite.Enabled = btnRead.Enabled = btnNotify.Enabled = false;
            GattDeviceService service = _services[cmbServices.SelectedIndex].GattDeviceService;
            GattCharacteristicsResult characteristicsResult = await service.GetCharacteristicsAsync(BluetoothCacheMode.Uncached);
            if (characteristicsResult.Status == GattCommunicationStatus.Success)
            {
                IReadOnlyList<GattCharacteristic> characteristics = characteristicsResult.Characteristics;
                foreach (var characteristic in characteristics)
                {
                    _characteristics.Add(new CharacteristicInformation(characteristic));
                }
                foreach (var characteristic in _characteristics)
                {
                    cmbCharacteristics.Items.Add($"{characteristic.Name}({characteristic.UUID})");
                }
                if (cmbCharacteristics.Items.Count > 0)
                    cmbCharacteristics.SelectedIndex = 0;
                btnGetOperation.Enabled = true;
                ShowLog("Get characteristics success! 获取特征值成功！");
            }
            else
                ShowLog("Get characteristics failed! 获取特征值失败！", true);
            LoadingHelper.CloseLoading();
        }

        private void btnGetOperation_Click(object sender, EventArgs e)
        {
            LoadingHelper.ShowLoading();
            ShowLog(string.Empty);
            btnWrite.Enabled = btnRead.Enabled = btnNotify.Enabled = false;
            GattCharacteristic characteristic = _characteristics[cmbCharacteristics.SelectedIndex].GattCharacteristic;
            GattCharacteristicProperties properties = characteristic.CharacteristicProperties;
            if (properties.HasFlag(GattCharacteristicProperties.Read))
                btnRead.Enabled = true;
            if (properties.HasFlag(GattCharacteristicProperties.Write))
                btnWrite.Enabled = true;
            if (properties.HasFlag(GattCharacteristicProperties.Notify))
                btnNotify.Enabled = true;
            ShowLog("Get operation success! 获取操作成功！");
            LoadingHelper.CloseLoading();
        }

        private async void btnWrite_Click(object sender, EventArgs e)
        {
            if (rtxtTransmitBuffer.Text.Length == 0)
            {
                ShowLog("Please input data first! 请先输入数据！", true);
                return;
            }
            else
            {
                LoadingHelper.ShowLoading();
                ShowLog(string.Empty);
                GattCharacteristic characteristic = _characteristics[cmbCharacteristics.SelectedIndex].GattCharacteristic;
                byte[] data = rtxtTransmitBuffer.Text.String2HexArray();
                IBuffer buffer = CryptographicBuffer.CreateFromByteArray(data);
                GattCommunicationStatus communicationStatus = await characteristic.WriteValueAsync(buffer, GattWriteOption.WriteWithoutResponse);
                if (communicationStatus == GattCommunicationStatus.Success)
                {
                    CryptographicBuffer.CopyToByteArray(buffer, out byte[] out_data);
                    ShowLog($"Write success! 写入成功！ {BitConverter.ToString(out_data)}");
                }
                else
                    ShowLog("Write failed! 写入失败！", true);
                LoadingHelper.CloseLoading();
            }
        }

        private async void btnRead_Click(object sender, EventArgs e)
        {
            LoadingHelper.ShowLoading();
            ShowLog(string.Empty);
            GattCharacteristic characteristic = _characteristics[cmbCharacteristics.SelectedIndex].GattCharacteristic;
            GattReadResult ReadResult = await characteristic.ReadValueAsync(BluetoothCacheMode.Uncached);
            if (ReadResult.Status == GattCommunicationStatus.Success)
            {
                CryptographicBuffer.CopyToByteArray(ReadResult.Value, out byte[] data);
                if (ckbDisplayASCII2.Checked)
                    rtxtReceiveBuffer.AppendText(Encoding.ASCII.GetString(data) + Environment.NewLine);
                else
                    rtxtReceiveBuffer.AppendText(data.HexArray2String() + Environment.NewLine);
                ShowLog("Read success! 读取成功！");
            }
            else
                ShowLog("Read failed! 读取失败！", true);
            LoadingHelper.CloseLoading();
        }

        private async void btnNotify_Click(object sender, EventArgs e)
        {
            LoadingHelper.ShowLoading();
            ShowLog(string.Empty);
            GattCharacteristic characteristic = _characteristics[cmbCharacteristics.SelectedIndex].GattCharacteristic;
            GattCommunicationStatus communicationStatus = await characteristic.WriteClientCharacteristicConfigurationDescriptorAsync(GattClientCharacteristicConfigurationDescriptorValue.Notify);
            if (communicationStatus == GattCommunicationStatus.Success)
            {
                characteristic.ValueChanged += OnValueChanged;
                ShowLog("Start notify success! 开始通知成功！");
            }
            else
                ShowLog("Start notify failed! 开始通知失败！", true);
            LoadingHelper.CloseLoading();
        }

        private void btnClear1_Click(object sender, EventArgs e)
        {
            rtxtReceiveBuffer.Clear();
        }

        private void btnClear2_Click(object sender, EventArgs e)
        {
            rtxtTransmitBuffer.Clear();
        }

        private void ShowLog(string log, bool isError = false)
        {
            if (isError)
                lblLog.ForeColor = Color.Red;
            else
                lblLog.ForeColor = Color.Green;

            if (string.IsNullOrEmpty(log))
                lblLog.Text = string.Empty;
            else
                lblLog.Text = $"[{DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss")}] {log}";
        }

        private void rtxtTransmitBuffer_KeyPress(object sender, KeyPressEventArgs e)
        {
            // 仅允许输入16进制字符
            if (e.KeyChar == '\b') return;
            if (e.KeyChar == '\r') return;
            if (!e.KeyChar.IsHex())
                e.Handled = true;
        }
    }
}
