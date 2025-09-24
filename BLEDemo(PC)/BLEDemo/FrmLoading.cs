using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace BLEDemo
{
    public partial class FrmLoading : Form
    {
        public FrmLoading()
        {
            InitializeComponent();
        }

        private void FrmLoading_FormClosing(object sender, FormClosingEventArgs e)
        {
            if (!IsDisposed)
                Dispose(true);
        }

        public void CloseLoading()
        {
            if (InvokeRequired)
                // 使用委托在主线程上调用 CloseLoading 方法
                Invoke(new MethodInvoker(CloseLoading));
            else
                Close();
        }
    }

    public class LoadingHelper
    {
        private delegate void CloseDelegate();
        private static FrmLoading _loading;
        private static readonly object _lock = new object();

        public static void ShowLoading()
        {
            if (_loading == null)
            {
                lock (_lock)
                {
                    if (_loading == null)
                    {
                        _loading = new FrmLoading();
                        _loading.FormClosing += (s, e) => _loading = null; // 确保关闭后释放引用
                        _loading.Show();
                    }
                }
            }
        }

        public static void CloseLoading()
        {
            if (_loading != null)
            {
                lock (_lock)
                {
                    if (_loading != null)
                    {
                        // 调用 FrmLoading 的 CloseLoading 方法来关闭窗体
                        _loading.CloseLoading();
                    }
                }
            }
        }
    }

}
