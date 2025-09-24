using System;
using Windows.Devices.Bluetooth.GenericAttributeProfile;

namespace BLEDemo
{
    public enum ServiceUuidType : ushort
    {
        /// <summary>
        /// 无服务类型
        /// </summary>
        None = 0,

        /// <summary>
        /// 警告通知服务
        /// 用于提供设备的警告通知功能
        /// </summary>
        AlertNotification = 0x1811,

        /// <summary>
        /// 电池服务
        /// 用于提供设备的电池状态信息
        /// </summary>
        Battery = 0x180F,

        /// <summary>
        /// 血压服务
        /// 用于提供血压监测数据
        /// </summary>
        BloodPressure = 0x1810,

        /// <summary>
        /// 当前时间服务
        /// 用于提供设备的当前时间信息
        /// </summary>
        CurrentTimeService = 0x1805,

        /// <summary>
        /// 自行车速度和踏频服务
        /// 用于提供自行车的速度和踏频数据
        /// </summary>
        CyclingSpeedandCadence = 0x1816,

        /// <summary>
        /// 设备信息服务
        /// 用于提供设备的制造商、型号、序列号等信息
        /// </summary>
        DeviceInformation = 0x180A,

        /// <summary>
        /// 通用访问服务
        /// 用于提供蓝牙设备的基本信息和控制
        /// </summary>
        GenericAccess = 0x1800,

        /// <summary>
        /// 通用属性服务
        /// 用于提供蓝牙设备的属性信息
        /// </summary>
        GenericAttribute = 0x1801,

        /// <summary>
        /// 血糖服务
        /// 用于提供血糖监测数据
        /// </summary>
        Glucose = 0x1808,

        /// <summary>
        /// 健康体温计服务
        /// 用于提供体温监测数据
        /// </summary>
        HealthThermometer = 0x1809,

        /// <summary>
        /// 心率服务
        /// 用于提供心率监测数据
        /// </summary>
        HeartRate = 0x180D,

        /// <summary>
        /// 人体接口设备服务
        /// 用于提供与人体接口设备的交互
        /// </summary>
        HumanInterfaceDevice = 0x1812,

        /// <summary>
        /// 即时警告服务
        /// 用于提供即时警告功能
        /// </summary>
        ImmediateAlert = 0x1802,

        /// <summary>
        /// 链路丢失服务
        /// 用于检测蓝牙连接的丢失并发出警告
        /// </summary>
        LinkLoss = 0x1803,

        /// <summary>
        /// 下一个夏令时变更服务
        /// 用于提供下一个夏令时变更的时间信息
        /// </summary>
        NextDSTChange = 0x1807,

        /// <summary>
        /// 手机警告状态服务
        /// 用于提供手机警告状态信息
        /// </summary>
        PhoneAlertStatus = 0x180E,

        /// <summary>
        /// 参考时间更新服务
        /// 用于提供参考时间的更新
        /// </summary>
        ReferenceTimeUpdateService = 0x1806,

        /// <summary>
        /// 跑步速度和踏频服务
        /// 用于提供跑步的速度和踏频数据
        /// </summary>
        RunningSpeedandCadence = 0x1814,

        /// <summary>
        /// 扫描参数服务
        /// 用于提供蓝牙扫描的参数设置
        /// </summary>
        ScanParameters = 0x1813,

        /// <summary>
        /// 发射功率服务
        /// 用于提供蓝牙设备的发射功率信息
        /// </summary>
        TxPower = 0x1804,

        /// <summary>
        /// 简单按键服务
        /// 用于提供简单按键的状态信息
        /// </summary>
        SimpleKeyService = 0xFFE0,

        /// <summary>
        /// 用户数据服务
        /// 用于提供用户的健康或活动数据
        /// </summary>
        UserData = 0x181C
    }

    public class ServiceInformation
    {
        public readonly GattDeviceService GattDeviceService;

        private string _uuid;
        public string UUID
        {
            get { return _uuid; }
            set { _uuid = value; }
        }

        private string _name;
        public string Name
        {
            get { return _name; }
            set { _name = value; }
        }

        private ushort _handle;
        public ushort Handle
        {
            get { return _handle; }
            set { _handle = value; }
        }

        public ServiceInformation(GattDeviceService gattDeviceService)
        {
            GattDeviceService = gattDeviceService;
            Init();
        }

        private void Init()
        {
            UUID = GattDeviceService.Uuid.ToString();
            if (UUID.IsGuid())
            {
                ServiceUuidType serviceUuidType;
                var bytes = Guid.Parse(UUID).ToByteArray();
                var shortUuid = (ushort)(bytes[0] | (bytes[1] << 8));
                Enum.TryParse(shortUuid.ToString(), out serviceUuidType);
                Name = serviceUuidType.ToString();
            }
            Handle = GattDeviceService.AttributeHandle;
        }
    }
}
