using System;
using System.Collections.Generic;
using System.Text;

namespace BLEDemo
{
    public static class Util
    {
        /// <summary>
        /// 是否为Guid
        /// </summary>
        /// <param name="thisValue"></param>
        /// <returns>True:是 False:否</returns>
        public static bool IsGuid(this object thisValue)
        {
            if (thisValue == null) return false;
            Guid outValue = Guid.Empty;
            return Guid.TryParse(thisValue.ToString(), out outValue);
        }

        /// <summary>
        /// 将十六进制数组转换为String类型
        /// </summary>
        /// <param name="arrData"></param>
        /// <param name="isSpace">是否用空格隔开</param>
        /// <returns></returns>
        public static string HexArray2String(this byte[] arrData, bool isSpace = true)
        {
            return arrData.HexArray2String(0, arrData.Length, isSpace);
        }

        /// <summary>
        /// Hex数组转换为字符串
        /// </summary>
        /// <param name="arrData"></param>
        /// <param name="index"></param>
        /// <param name="len"></param>
        /// <param name="isSpace"></param>
        /// <returns></returns>
        public static string HexArray2String(this byte[] arrData, int index, int len, bool isSpace = true)
        {
            if (len == 0)
                return string.Empty;
            if (len == 1)
                return arrData[0].ToString("X2");
            StringBuilder sb = new StringBuilder(len * 3);
            if (!isSpace)
                sb = new StringBuilder(len * 2);
            if (arrData.Length > 0)
            {
                len = index + len - 1;
                for (int i = index; i < len; ++i)
                {
                    sb.Append(arrData[i].ToString("X2"));
                    if (isSpace) sb.Append(' ');
                }
                sb.Append(arrData[len].ToString("X2"));
            }
            return sb.ToString();
        }

        public static readonly byte[] EmptyArray = new byte[0];
        /// <summary>
        /// 将String类型转换为16进制的数组
        /// </summary>
        /// <param name="value"></param>
        /// <returns></returns>
        public static byte[] String2HexArray(this string value)
        {
            if (string.IsNullOrEmpty(value))
                return EmptyArray;

            List<byte> lstHex = new List<byte>(1024);
            String2HexArray(value, lstHex);
            return lstHex.ToArray();
        }

        /// <summary>
        /// 字符串转换为Hex数组
        /// </summary>
        /// <param name="value"></param>
        /// <param name="lstHex"></param>
        /// <exception cref="FormatException"></exception>
        public static void String2HexArray(string value, List<byte> lstHex)
        {
            // 当前状态
            // ，0 表示当前字节还没数据，等待接收第一个4位数据
            // ，1 表示已经接收第一个4位数据，等待接收第二个4位数据
            int iState = 0;
            byte btCur = 0, btTmp = 0;
            foreach (char ch in value)
            {
                switch (iState)
                {
                    case 0:
                        if (IsSpec(ch))
                            continue;
                        if (!IsHex(ch))
                            throw new FormatException("错误的十六进制字符串'" + value + "'");
                        btCur = Char2Hex(ch);
                        iState = 1;
                        break;
                    case 1:
                        if (IsSpec(ch))
                        {
                            lstHex.Add(btCur);
                            iState = 0;
                            continue;
                        }
                        if (!IsHex(ch))
                        {
                            throw new FormatException("错误的十六进制字符串'" + value + "'");
                        }
                        btTmp = Char2Hex(ch);
                        btCur = (byte)((btCur << 4) + btTmp);
                        lstHex.Add(btCur);
                        iState = 0;
                        break;
                    default:
                        throw new FormatException("错误的十六进制字符串'" + value + "'");
                }
            }
            if (iState == 1)
            {
                lstHex.Add(btCur);
            }
        }

        /// <summary>
        /// 将Char转换为Hex
        /// </summary>
        /// <param name="ch"></param>
        /// <returns></returns>
        public static byte Char2Hex(this char ch)
        {
            byte btHex = 0;
            if (ch >= '0' && ch <= '9')
            {
                btHex = (byte)(ch - '0');
            }
            else if (ch >= 'A' && ch <= 'Z')
            {
                btHex = (byte)(ch - 'A' + 10);
            }
            else if (ch >= 'a' && ch <= 'z')
            {
                btHex = (byte)(ch - 'a' + 10);
            }
            return btHex;
        }

        /// <summary>
        /// 是否为空
        /// </summary>
        /// <param name="ch"></param>
        /// <returns></returns>
        public static bool IsSpec(this char ch)
        {
            return ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n';
        }

        /// <summary>
        /// 是否为十六进制
        /// </summary>
        /// <param name="ch"></param>
        /// <returns></returns>
        public static bool IsHex(this char ch)
        {
            return ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f';
        }
    }
}
