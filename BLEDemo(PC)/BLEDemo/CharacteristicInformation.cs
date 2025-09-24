using System;
using Windows.Devices.Bluetooth.GenericAttributeProfile;

namespace BLEDemo
{
    public enum CharacteristicUuidType : ushort
    {
        /// <summary>
        /// 无特征UUID
        /// </summary>
        None = 0,

        /// <summary>
        /// 警告类别ID
        /// 用于标识警告的类别
        /// </summary>
        AlertCategoryID = 0x2A43,

        /// <summary>
        /// 警告类别ID位掩码
        /// 用于设置和获取警告类别的位掩码
        /// </summary>
        AlertCategoryIDBitMask = 0x2A42,

        /// <summary>
        /// 警告级别
        /// 用于指示警告的级别（无警告、中警告、高警告）
        /// </summary>
        AlertLevel = 0x2A06,

        /// <summary>
        /// 警告通知控制点
        /// 用于控制警告通知的行为
        /// </summary>
        AlertNotificationControlPoint = 0x2A44,

        /// <summary>
        /// 警告状态
        /// 用于指示当前的警告状态
        /// </summary>
        AlertStatus = 0x2A3F,

        /// <summary>
        /// 外观
        /// 用于描述设备的外观
        /// </summary>
        Appearance = 0x2A01,

        /// <summary>
        /// 电池电量
        /// 用于报告电池的剩余电量
        /// </summary>
        BatteryLevel = 0x2A19,

        /// <summary>
        /// 血压特性
        /// 用于报告血压测量的特性
        /// </summary>
        BloodPressureFeature = 0x2A49,

        /// <summary>
        /// 血压测量
        /// 用于报告血压测量的数据
        /// </summary>
        BloodPressureMeasurement = 0x2A35,

        /// <summary>
        /// 体感器位置
        /// 用于指示体感器的位置
        /// </summary>
        BodySensorLocation = 0x2A38,

        /// <summary>
        /// 启动键盘输入报告
        /// 用于报告启动键盘的输入数据
        /// </summary>
        BootKeyboardInputReport = 0x2A22,

        /// <summary>
        /// 启动键盘输出报告
        /// 用于报告启动键盘的输出数据
        /// </summary>
        BootKeyboardOutputReport = 0x2A32,

        /// <summary>
        /// 启动鼠标输入报告
        /// 用于报告启动鼠标的输入数据
        /// </summary>
        BootMouseInputReport = 0x2A33,

        /// <summary>
        /// 自行车速度和踏频特性
        /// 用于报告自行车速度和踏频的特性
        /// </summary>
        CSCFeature = 0x2A5C,

        /// <summary>
        /// 自行车速度和踏频测量
        /// 用于报告自行车速度和踏频的测量数据
        /// </summary>
        CSCMeasurement = 0x2A5B,

        /// <summary>
        /// 当前时间
        /// 用于报告当前的时间
        /// </summary>
        CurrentTime = 0x2A2B,

        /// <summary>
        /// 日期时间
        /// 用于报告完整的日期和时间
        /// </summary>
        DateTime = 0x2A08,

        /// <summary>
        /// 日期时间
        /// 用于报告日期的特定部分
        /// </summary>
        DayDateTime = 0x2A0A,

        /// <summary>
        /// 星期几
        /// 用于报告当前是星期几
        /// </summary>
        DayofWeek = 0x2A09,

        /// <summary>
        /// 设备名称
        /// 用于报告设备的名称
        /// </summary>
        DeviceName = 0x2A00,

        /// <summary>
        /// 夏令时偏移量
        /// 用于报告夏令时的偏移量
        /// </summary>
        DSTOffset = 0x2A0D,

        /// <summary>
        /// 精确时间（256单位）
        /// 用于报告精确时间，单位为256分之一秒
        /// </summary>
        ExactTime256 = 0x2A0C,

        /// <summary>
        /// 固件版本字符串
        /// 用于报告设备的固件版本
        /// </summary>
        FirmwareRevisionString = 0x2A26,

        /// <summary>
        /// 血糖特性
        /// 用于报告血糖测量的特性
        /// </summary>
        GlucoseFeature = 0x2A51,

        /// <summary>
        /// 血糖测量
        /// 用于报告血糖测量的数据
        /// </summary>
        GlucoseMeasurement = 0x2A18,

        /// <summary>
        /// 血糖测量上下文
        /// 用于报告血糖测量的上下文信息
        /// </summary>
        GlucoseMeasurementContext = 0x2A34,

        /// <summary>
        /// 硬件版本字符串
        /// 用于报告设备的硬件版本
        /// </summary>
        HardwareRevisionString = 0x2A27,

        /// <summary>
        /// 心率控制点
        /// 用于控制心率测量的行为
        /// </summary>
        HeartRateControlPoint = 0x2A39,

        /// <summary>
        /// 心率测量
        /// 用于报告心率测量的数据
        /// </summary>
        HeartRateMeasurement = 0x2A37,

        /// <summary>
        /// HID控制点
        /// 用于控制HID设备的行为
        /// </summary>
        HIDControlPoint = 0x2A4C,

        /// <summary>
        /// HID信息
        /// 用于报告HID设备的信息
        /// </summary>
        HIDInformation = 0x2A4A,

        /// <summary>
        /// IEEE 11073-20601监管认证数据列表
        /// 用于报告IEEE 11073-20601标准的监管认证数据
        /// </summary>
        IEEE11073_20601RegulatoryCertificationDataList = 0x2A2A,

        /// <summary>
        /// 中间袖带压力
        /// 用于报告中间的袖带压力数据
        /// </summary>
        IntermediateCuffPressure = 0x2A36,

        /// <summary>
        /// 中间温度
        /// 用于报告中间的温度数据
        /// </summary>
        IntermediateTemperature = 0x2A1E,

        /// <summary>
        /// 本地时间信息
        /// 用于报告设备的本地时间信息
        /// </summary>
        LocalTimeInformation = 0x2A0F,

        /// <summary>
        /// 制造商名称字符串
        /// 用于报告设备的制造商名称
        /// </summary>
        ManufacturerNameString = 0x2A29,

        /// <summary>
        /// 测量间隔
        /// 用于报告测量的间隔时间
        /// </summary>
        MeasurementInterval = 0x2A21,

        /// <summary>
        /// 型号编号字符串
        /// 用于报告设备的型号编号
        /// </summary>
        ModelNumberString = 0x2A24,

        /// <summary>
        /// 新警告
        /// 用于报告新警告的通知
        /// </summary>
        NewAlert = 0x2A46,

        /// <summary>
        /// 外设首选连接参数
        /// 用于报告外设的首选连接参数
        /// </summary>
        PeripheralPreferredConnectionParameters = 0x2A04,

        /// <summary>
        /// 外设隐私标志
        /// 用于指示外设的隐私标志
        /// </summary>
        PeripheralPrivacyFlag = 0x2A02,

        /// <summary>
        /// PnP ID
        /// 用于报告设备的PnP ID
        /// </summary>
        PnPID = 0x2A50,

        /// <summary>
        /// 协议模式
        /// 用于控制设备的协议模式
        /// </summary>
        ProtocolMode = 0x2A4E,

        /// <summary>
        /// 重新连接地址
        /// 用于报告设备的重新连接地址
        /// </summary>
        ReconnectionAddress = 0x2A03,

        /// <summary>
        /// 记录访问控制点
        /// 用于控制对设备记录的访问
        /// </summary>
        RecordAccessControlPoint = 0x2A52,

        /// <summary>
        /// 参考时间信息
        /// 用于报告参考时间信息
        /// </summary>
        ReferenceTimeInformation = 0x2A14,

        /// <summary>
        /// 报告
        /// 用于报告特定的输入或输出数据
        /// </summary>
        Report = 0x2A4D,

        /// <summary>
        /// 报告映射
        /// 用于报告输入或输出报告的映射
        /// </summary>
        ReportMap = 0x2A4B,

        /// <summary>
        /// 铃声控制点
        /// 用于控制设备的铃声行为
        /// </summary>
        RingerControlPoint = 0x2A40,

        /// <summary>
        /// 铃声设置
        /// 用于报告设备的铃声设置
        /// </summary>
        RingerSetting = 0x2A41,

        /// <summary>
        /// RSC特性
        /// 用于报告跑步速度和踏频的特性
        /// </summary>
        RSCFeature = 0x2A54,

        /// <summary>
        /// RSC测量
        /// 用于报告跑步速度和踏频的测量数据
        /// </summary>
        RSCMeasurement = 0x2A53,

        /// <summary>
        /// SC控制点
        /// 用于控制SC设备的行为
        /// </summary>
        SCControlPoint = 0x2A55,

        /// <summary>
        /// 扫描间隔窗口
        /// 用于报告扫描的间隔和窗口设置
        /// </summary>
        ScanIntervalWindow = 0x2A4F,

        /// <summary>
        /// 扫描刷新
        /// 用于刷新扫描参数
        /// </summary>
        ScanRefresh = 0x2A31,

        /// <summary>
        /// 传感器位置
        /// 用于报告传感器的位置
        /// </summary>
        SensorLocation = 0x2A5D,

        /// <summary>
        /// 序列号字符串
        /// 用于报告设备的序列号
        /// </summary>
        SerialNumberString = 0x2A25,

        /// <summary>
        /// 服务更改
        /// 用于通知设备的服务更改
        /// </summary>
        ServiceChanged = 0x2A05,

        /// <summary>
        /// 软件版本字符串
        /// 用于报告设备的软件版本
        /// </summary>
        SoftwareRevisionString = 0x2A28,

        /// <summary>
        /// 支持的新警告类别
        /// 用于报告支持的新警告类别
        /// </summary>
        SupportedNewAlertCategory = 0x2A47,

        /// <summary>
        /// 支持的未读警告类别
        /// 用于报告支持的未读警告类别
        /// </summary>
        SupportedUnreadAlertCategory = 0x2A48,

        /// <summary>
        /// 系统ID
        /// 用于报告设备的系统ID
        /// </summary>
        SystemID = 0x2A23,

        /// <summary>
        /// 温度测量
        /// 用于报告温度测量的数据
        /// </summary>
        TemperatureMeasurement = 0x2A1C,

        /// <summary>
        /// 温度类型
        /// 用于报告温度的类型
        /// </summary>
        TemperatureType = 0x2A1D,

        /// <summary>
        /// 时间精度
        /// 用于报告时间的精度
        /// </summary>
        TimeAccuracy = 0x2A12,

        /// <summary>
        /// 时间源
        /// 用于报告时间源的信息
        /// </summary>
        TimeSource = 0x2A13,

        /// <summary>
        /// 时间更新控制点
        /// 用于控制时间更新的行为
        /// </summary>
        TimeUpdateControlPoint = 0x2A16,

        /// <summary>
        /// 时间更新状态
        /// 用于报告时间更新的状态
        /// </summary>
        TimeUpdateState = 0x2A17,

        /// <summary>
        /// 带夏令时的时间
        /// 用于报告带夏令时的时间
        /// </summary>
        TimewithDST = 0x2A11,

        /// <summary>
        /// 时区
        /// 用于报告设备的时区信息
        /// </summary>
        TimeZone = 0x2A0E,

        /// <summary>
        /// 发射功率水平
        /// 用于报告设备的发射功率水平
        /// </summary>
        TxPowerLevel = 0x2A07,

        /// <summary>
        /// 未读警告状态
        /// 用于报告未读警告的状态
        /// </summary>
        UnreadAlertStatus = 0x2A45,

        /// <summary>
        /// 聚合输入
        /// 用于报告聚合输入数据
        /// </summary>
        AggregateInput = 0x2A5A,

        /// <summary>
        /// 模拟输入
        /// 用于报告模拟输入数据
        /// </summary>
        AnalogInput = 0x2A58,

        /// <summary>
        /// 模拟输出
        /// 用于报告模拟输出数据
        /// </summary>
        AnalogOutput = 0x2A59,

        /// <summary>
        /// 自行车功率控制点
        /// 用于控制自行车功率的行为
        /// </summary>
        CyclingPowerControlPoint = 0x2A66,

        /// <summary>
        /// 自行车功率特性
        /// 用于报告自行车功率的特性
        /// </summary>
        CyclingPowerFeature = 0x2A65,

        /// <summary>
        /// 自行车功率测量
        /// 用于报告自行车功率的测量数据
        /// </summary>
        CyclingPowerMeasurement = 0x2A63,

        /// <summary>
        /// 自行车功率矢量
        /// 用于报告自行车功率的矢量数据
        /// </summary>
        CyclingPowerVector = 0x2A64,

        /// <summary>
        /// 数字输入
        /// 用于报告数字输入数据
        /// </summary>
        DigitalInput = 0x2A56,

        /// <summary>
        /// 数字输出
        /// 用于报告数字输出数据
        /// </summary>
        DigitalOutput = 0x2A57,

        /// <summary>
        /// 精确时间（100单位）
        /// 用于报告精确时间，单位为100分之一秒
        /// </summary>
        ExactTime100 = 0x2A0B,

        /// <summary>
        /// LN控制点
        /// 用于控制LN设备的行为
        /// </summary>
        LNControlPoint = 0x2A6B,

        /// <summary>
        /// LN特性
        /// 用于报告LN设备的特性
        /// </summary>
        LNFeature = 0x2A6A,

        /// <summary>
        /// 位置和速度
        /// 用于报告位置和速度数据
        /// </summary>
        LocationandSpeed = 0x2A67,

        /// <summary>
        /// 导航
        /// 用于报告导航数据
        /// </summary>
        Navigation = 0x2A68,

        /// <summary>
        /// 网络可用性
        /// 用于报告网络的可用性状态
        /// </summary>
        NetworkAvailability = 0x2A3E,

        /// <summary>
        /// 位置质量
        /// 用于报告位置的质量信息
        /// </summary>
        PositionQuality = 0x2A69,

        /// <summary>
        /// 科学温度（摄氏度）
        /// 用于报告科学温度的摄氏度值
        /// </summary>
        ScientificTemperatureinCelsius = 0x2A3C,

        /// <summary>
        /// 辅助时区
        /// 用于报告辅助时区信息
        /// </summary>
        SecondaryTimeZone = 0x2A10,

        /// <summary>
        /// 字符串
        /// 用于报告通用字符串数据
        /// </summary>
        String = 0x2A3D,

        /// <summary>
        /// 温度（摄氏度）
        /// 用于报告温度的摄氏度值
        /// </summary>
        TemperatureinCelsius = 0x2A1F,

        /// <summary>
        /// 温度（华氏度）
        /// 用于报告温度的华氏度值
        /// </summary>
        TemperatureinFahrenheit = 0x2A20,

        /// <summary>
        /// 时间广播
        /// 用于报告时间广播数据
        /// </summary>
        TimeBroadcast = 0x2A15,

        /// <summary>
        /// 电池电量状态
        /// 用于报告电池的电量状态
        /// </summary>
        BatteryLevelState = 0x2A1B,

        /// <summary>
        /// 电池电源状态
        /// 用于报告电池的电源状态
        /// </summary>
        BatteryPowerState = 0x2A1A,

        /// <summary>
        /// 持续脉搏血氧测量
        /// 用于报告持续脉搏血氧测量的数据
        /// </summary>
        PulseOximetryContinuousMeasurement = 0x2A5F,

        /// <summary>
        /// 脉搏血氧控制点
        /// 用于控制脉搏血氧的行为
        /// </summary>
        PulseOximetryControlPoint = 0x2A62,

        /// <summary>
        /// 脉搏血氧特性
        /// 用于报告脉搏血氧的特性
        /// </summary>
        PulseOximetryFeatures = 0x2A61,

        /// <summary>
        /// 脉搏血氧脉冲事件
        /// 用于报告脉搏血氧的脉冲事件数据
        /// </summary>
        PulseOximetryPulsatileEvent = 0x2A60,

        /// <summary>
        /// 简单按键状态
        /// 用于报告简单按键的状态
        /// </summary>
        SimpleKeyState = 0xFFE1
    }

    public class CharacteristicInformation
    {
        public readonly GattCharacteristic GattCharacteristic;

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

        public CharacteristicInformation(GattCharacteristic characteristic)
        {
            GattCharacteristic = characteristic;
            Init();
        }

        private void Init()
        {
            UUID = GattCharacteristic.Uuid.ToString();
            if (UUID.IsGuid())
            {
                CharacteristicUuidType characteristicUuidType;
                var bytes = Guid.Parse(UUID).ToByteArray();
                var shortUuid = (ushort)(bytes[0] | (bytes[1] << 8));
                Enum.TryParse(shortUuid.ToString(), out characteristicUuidType);
                Name = characteristicUuidType.ToString();
            }
            else if (GattCharacteristic.UserDescription != null)
            {
                Name = GattCharacteristic.UserDescription;
            }
            Handle = GattCharacteristic.AttributeHandle;
        }
    }
}
