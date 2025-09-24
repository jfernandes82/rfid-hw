namespace BLEDemo
{
    partial class FrmMain
    {
        /// <summary>
        /// 必需的设计器变量。
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// 清理所有正在使用的资源。
        /// </summary>
        /// <param name="disposing">如果应释放托管资源，为 true；否则为 false。</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows 窗体设计器生成的代码

        /// <summary>
        /// 设计器支持所需的方法 - 不要修改
        /// 使用代码编辑器修改此方法的内容。
        /// </summary>
        private void InitializeComponent()
        {
            this.tableLayoutPanel1 = new System.Windows.Forms.TableLayoutPanel();
            this.btnOpen = new System.Windows.Forms.Button();
            this.cmbDevices = new System.Windows.Forms.ComboBox();
            this.btnRefresh = new System.Windows.Forms.Button();
            this.btnGetServices = new System.Windows.Forms.Button();
            this.btnGetCharacteriatics = new System.Windows.Forms.Button();
            this.cmbServices = new System.Windows.Forms.ComboBox();
            this.cmbCharacteristics = new System.Windows.Forms.ComboBox();
            this.lblReceiveBuffer = new System.Windows.Forms.Label();
            this.lblTransmitBuffer = new System.Windows.Forms.Label();
            this.ckbDisplayASCII1 = new System.Windows.Forms.CheckBox();
            this.ckbDisplayASCII2 = new System.Windows.Forms.CheckBox();
            this.btnClear1 = new System.Windows.Forms.Button();
            this.btnClear2 = new System.Windows.Forms.Button();
            this.rtxtReceiveBuffer = new System.Windows.Forms.RichTextBox();
            this.rtxtTransmitBuffer = new System.Windows.Forms.RichTextBox();
            this.lblLog = new System.Windows.Forms.Label();
            this.panel1 = new System.Windows.Forms.Panel();
            this.btnNotify = new System.Windows.Forms.Button();
            this.btnRead = new System.Windows.Forms.Button();
            this.btnWrite = new System.Windows.Forms.Button();
            this.btnGetOperation = new System.Windows.Forms.Button();
            this.tableLayoutPanel1.SuspendLayout();
            this.panel1.SuspendLayout();
            this.SuspendLayout();
            // 
            // tableLayoutPanel1
            // 
            this.tableLayoutPanel1.ColumnCount = 9;
            this.tableLayoutPanel1.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle());
            this.tableLayoutPanel1.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Percent, 50F));
            this.tableLayoutPanel1.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle());
            this.tableLayoutPanel1.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle());
            this.tableLayoutPanel1.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle());
            this.tableLayoutPanel1.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Percent, 50F));
            this.tableLayoutPanel1.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle());
            this.tableLayoutPanel1.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle());
            this.tableLayoutPanel1.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Absolute, 1F));
            this.tableLayoutPanel1.Controls.Add(this.btnOpen, 7, 0);
            this.tableLayoutPanel1.Controls.Add(this.cmbDevices, 0, 0);
            this.tableLayoutPanel1.Controls.Add(this.btnRefresh, 6, 0);
            this.tableLayoutPanel1.Controls.Add(this.btnGetServices, 0, 1);
            this.tableLayoutPanel1.Controls.Add(this.btnGetCharacteriatics, 4, 1);
            this.tableLayoutPanel1.Controls.Add(this.cmbServices, 1, 1);
            this.tableLayoutPanel1.Controls.Add(this.cmbCharacteristics, 5, 1);
            this.tableLayoutPanel1.Controls.Add(this.lblReceiveBuffer, 0, 3);
            this.tableLayoutPanel1.Controls.Add(this.lblTransmitBuffer, 4, 3);
            this.tableLayoutPanel1.Controls.Add(this.ckbDisplayASCII1, 2, 3);
            this.tableLayoutPanel1.Controls.Add(this.ckbDisplayASCII2, 5, 3);
            this.tableLayoutPanel1.Controls.Add(this.btnClear1, 3, 3);
            this.tableLayoutPanel1.Controls.Add(this.btnClear2, 7, 3);
            this.tableLayoutPanel1.Controls.Add(this.rtxtReceiveBuffer, 0, 4);
            this.tableLayoutPanel1.Controls.Add(this.rtxtTransmitBuffer, 4, 4);
            this.tableLayoutPanel1.Controls.Add(this.lblLog, 0, 5);
            this.tableLayoutPanel1.Controls.Add(this.panel1, 0, 2);
            this.tableLayoutPanel1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.tableLayoutPanel1.Location = new System.Drawing.Point(0, 0);
            this.tableLayoutPanel1.Name = "tableLayoutPanel1";
            this.tableLayoutPanel1.Padding = new System.Windows.Forms.Padding(10);
            this.tableLayoutPanel1.RowCount = 7;
            this.tableLayoutPanel1.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Absolute, 30F));
            this.tableLayoutPanel1.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Absolute, 30F));
            this.tableLayoutPanel1.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Absolute, 40F));
            this.tableLayoutPanel1.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Absolute, 30F));
            this.tableLayoutPanel1.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Percent, 100F));
            this.tableLayoutPanel1.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Absolute, 40F));
            this.tableLayoutPanel1.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Absolute, 1F));
            this.tableLayoutPanel1.Size = new System.Drawing.Size(800, 450);
            this.tableLayoutPanel1.TabIndex = 0;
            // 
            // btnOpen
            // 
            this.btnOpen.Dock = System.Windows.Forms.DockStyle.Fill;
            this.btnOpen.Location = new System.Drawing.Point(711, 13);
            this.btnOpen.Name = "btnOpen";
            this.btnOpen.Size = new System.Drawing.Size(75, 24);
            this.btnOpen.TabIndex = 2;
            this.btnOpen.Text = "OPEN";
            this.btnOpen.UseVisualStyleBackColor = true;
            this.btnOpen.Click += new System.EventHandler(this.btnOpen_Click);
            // 
            // cmbDevices
            // 
            this.tableLayoutPanel1.SetColumnSpan(this.cmbDevices, 6);
            this.cmbDevices.Dock = System.Windows.Forms.DockStyle.Fill;
            this.cmbDevices.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.cmbDevices.FormattingEnabled = true;
            this.cmbDevices.Location = new System.Drawing.Point(13, 15);
            this.cmbDevices.Margin = new System.Windows.Forms.Padding(3, 5, 3, 3);
            this.cmbDevices.Name = "cmbDevices";
            this.cmbDevices.Size = new System.Drawing.Size(663, 20);
            this.cmbDevices.TabIndex = 0;
            // 
            // btnRefresh
            // 
            this.btnRefresh.Dock = System.Windows.Forms.DockStyle.Right;
            this.btnRefresh.Location = new System.Drawing.Point(682, 13);
            this.btnRefresh.Name = "btnRefresh";
            this.btnRefresh.Size = new System.Drawing.Size(23, 24);
            this.btnRefresh.TabIndex = 1;
            this.btnRefresh.Text = "↻";
            this.btnRefresh.UseVisualStyleBackColor = true;
            this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
            // 
            // btnGetServices
            // 
            this.btnGetServices.Dock = System.Windows.Forms.DockStyle.Fill;
            this.btnGetServices.Enabled = false;
            this.btnGetServices.Location = new System.Drawing.Point(13, 43);
            this.btnGetServices.Name = "btnGetServices";
            this.btnGetServices.Size = new System.Drawing.Size(99, 24);
            this.btnGetServices.TabIndex = 3;
            this.btnGetServices.Text = "GET SERVICES";
            this.btnGetServices.UseVisualStyleBackColor = true;
            this.btnGetServices.Click += new System.EventHandler(this.btnGetServices_Click);
            // 
            // btnGetCharacteriatics
            // 
            this.btnGetCharacteriatics.AutoSize = true;
            this.btnGetCharacteriatics.Dock = System.Windows.Forms.DockStyle.Fill;
            this.btnGetCharacteriatics.Enabled = false;
            this.btnGetCharacteriatics.Location = new System.Drawing.Point(427, 43);
            this.btnGetCharacteriatics.Name = "btnGetCharacteriatics";
            this.btnGetCharacteriatics.Size = new System.Drawing.Size(129, 24);
            this.btnGetCharacteriatics.TabIndex = 4;
            this.btnGetCharacteriatics.Text = "GET CHARACTERISTICS";
            this.btnGetCharacteriatics.UseVisualStyleBackColor = true;
            this.btnGetCharacteriatics.Click += new System.EventHandler(this.btnGetCharacteriatics_Click);
            // 
            // cmbServices
            // 
            this.tableLayoutPanel1.SetColumnSpan(this.cmbServices, 3);
            this.cmbServices.Dock = System.Windows.Forms.DockStyle.Fill;
            this.cmbServices.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.cmbServices.FormattingEnabled = true;
            this.cmbServices.Location = new System.Drawing.Point(118, 45);
            this.cmbServices.Margin = new System.Windows.Forms.Padding(3, 5, 3, 3);
            this.cmbServices.Name = "cmbServices";
            this.cmbServices.Size = new System.Drawing.Size(303, 20);
            this.cmbServices.TabIndex = 5;
            // 
            // cmbCharacteristics
            // 
            this.tableLayoutPanel1.SetColumnSpan(this.cmbCharacteristics, 3);
            this.cmbCharacteristics.Dock = System.Windows.Forms.DockStyle.Fill;
            this.cmbCharacteristics.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.cmbCharacteristics.FormattingEnabled = true;
            this.cmbCharacteristics.Location = new System.Drawing.Point(562, 45);
            this.cmbCharacteristics.Margin = new System.Windows.Forms.Padding(3, 5, 3, 3);
            this.cmbCharacteristics.Name = "cmbCharacteristics";
            this.cmbCharacteristics.Size = new System.Drawing.Size(224, 20);
            this.cmbCharacteristics.TabIndex = 6;
            // 
            // lblReceiveBuffer
            // 
            this.lblReceiveBuffer.AutoSize = true;
            this.lblReceiveBuffer.Dock = System.Windows.Forms.DockStyle.Fill;
            this.lblReceiveBuffer.Location = new System.Drawing.Point(13, 110);
            this.lblReceiveBuffer.Name = "lblReceiveBuffer";
            this.lblReceiveBuffer.Padding = new System.Windows.Forms.Padding(10, 0, 0, 0);
            this.lblReceiveBuffer.Size = new System.Drawing.Size(99, 30);
            this.lblReceiveBuffer.TabIndex = 11;
            this.lblReceiveBuffer.Text = "Receive Buffer";
            this.lblReceiveBuffer.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
            // 
            // lblTransmitBuffer
            // 
            this.lblTransmitBuffer.AutoSize = true;
            this.lblTransmitBuffer.Dock = System.Windows.Forms.DockStyle.Fill;
            this.lblTransmitBuffer.Location = new System.Drawing.Point(427, 110);
            this.lblTransmitBuffer.Name = "lblTransmitBuffer";
            this.lblTransmitBuffer.Padding = new System.Windows.Forms.Padding(10, 0, 0, 0);
            this.lblTransmitBuffer.Size = new System.Drawing.Size(129, 30);
            this.lblTransmitBuffer.TabIndex = 12;
            this.lblTransmitBuffer.Text = "Transmit Buffer";
            this.lblTransmitBuffer.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
            // 
            // ckbDisplayASCII1
            // 
            this.ckbDisplayASCII1.AutoSize = true;
            this.ckbDisplayASCII1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.ckbDisplayASCII1.Location = new System.Drawing.Point(238, 113);
            this.ckbDisplayASCII1.Name = "ckbDisplayASCII1";
            this.ckbDisplayASCII1.Size = new System.Drawing.Size(102, 24);
            this.ckbDisplayASCII1.TabIndex = 13;
            this.ckbDisplayASCII1.Text = "Display ASCII";
            this.ckbDisplayASCII1.UseVisualStyleBackColor = true;
            // 
            // ckbDisplayASCII2
            // 
            this.ckbDisplayASCII2.AutoSize = true;
            this.tableLayoutPanel1.SetColumnSpan(this.ckbDisplayASCII2, 2);
            this.ckbDisplayASCII2.Dock = System.Windows.Forms.DockStyle.Right;
            this.ckbDisplayASCII2.Location = new System.Drawing.Point(603, 113);
            this.ckbDisplayASCII2.Name = "ckbDisplayASCII2";
            this.ckbDisplayASCII2.Size = new System.Drawing.Size(102, 24);
            this.ckbDisplayASCII2.TabIndex = 14;
            this.ckbDisplayASCII2.Text = "Display ASCII";
            this.ckbDisplayASCII2.UseVisualStyleBackColor = true;
            this.ckbDisplayASCII2.Visible = false;
            // 
            // btnClear1
            // 
            this.btnClear1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.btnClear1.Location = new System.Drawing.Point(346, 113);
            this.btnClear1.Name = "btnClear1";
            this.btnClear1.Size = new System.Drawing.Size(75, 24);
            this.btnClear1.TabIndex = 15;
            this.btnClear1.Text = "CLEAR";
            this.btnClear1.UseVisualStyleBackColor = true;
            this.btnClear1.Click += new System.EventHandler(this.btnClear1_Click);
            // 
            // btnClear2
            // 
            this.btnClear2.Dock = System.Windows.Forms.DockStyle.Fill;
            this.btnClear2.Location = new System.Drawing.Point(711, 113);
            this.btnClear2.Name = "btnClear2";
            this.btnClear2.Size = new System.Drawing.Size(75, 24);
            this.btnClear2.TabIndex = 16;
            this.btnClear2.Text = "CLEAR";
            this.btnClear2.UseVisualStyleBackColor = true;
            this.btnClear2.Click += new System.EventHandler(this.btnClear2_Click);
            // 
            // rtxtReceiveBuffer
            // 
            this.rtxtReceiveBuffer.BackColor = System.Drawing.SystemColors.Window;
            this.rtxtReceiveBuffer.BorderStyle = System.Windows.Forms.BorderStyle.None;
            this.tableLayoutPanel1.SetColumnSpan(this.rtxtReceiveBuffer, 4);
            this.rtxtReceiveBuffer.Dock = System.Windows.Forms.DockStyle.Fill;
            this.rtxtReceiveBuffer.Location = new System.Drawing.Point(13, 143);
            this.rtxtReceiveBuffer.Name = "rtxtReceiveBuffer";
            this.rtxtReceiveBuffer.ReadOnly = true;
            this.rtxtReceiveBuffer.ScrollBars = System.Windows.Forms.RichTextBoxScrollBars.ForcedVertical;
            this.rtxtReceiveBuffer.Size = new System.Drawing.Size(408, 253);
            this.rtxtReceiveBuffer.TabIndex = 17;
            this.rtxtReceiveBuffer.Text = "";
            // 
            // rtxtTransmitBuffer
            // 
            this.rtxtTransmitBuffer.BackColor = System.Drawing.SystemColors.Window;
            this.rtxtTransmitBuffer.BorderStyle = System.Windows.Forms.BorderStyle.None;
            this.tableLayoutPanel1.SetColumnSpan(this.rtxtTransmitBuffer, 4);
            this.rtxtTransmitBuffer.Dock = System.Windows.Forms.DockStyle.Fill;
            this.rtxtTransmitBuffer.ImeMode = System.Windows.Forms.ImeMode.Disable;
            this.rtxtTransmitBuffer.Location = new System.Drawing.Point(427, 143);
            this.rtxtTransmitBuffer.Name = "rtxtTransmitBuffer";
            this.rtxtTransmitBuffer.ScrollBars = System.Windows.Forms.RichTextBoxScrollBars.ForcedVertical;
            this.rtxtTransmitBuffer.Size = new System.Drawing.Size(359, 253);
            this.rtxtTransmitBuffer.TabIndex = 18;
            this.rtxtTransmitBuffer.Text = "";
            this.rtxtTransmitBuffer.KeyPress += new System.Windows.Forms.KeyPressEventHandler(this.rtxtTransmitBuffer_KeyPress);
            // 
            // lblLog
            // 
            this.lblLog.AutoSize = true;
            this.tableLayoutPanel1.SetColumnSpan(this.lblLog, 8);
            this.lblLog.Dock = System.Windows.Forms.DockStyle.Fill;
            this.lblLog.Location = new System.Drawing.Point(13, 399);
            this.lblLog.Name = "lblLog";
            this.lblLog.Size = new System.Drawing.Size(773, 40);
            this.lblLog.TabIndex = 19;
            this.lblLog.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
            // 
            // panel1
            // 
            this.tableLayoutPanel1.SetColumnSpan(this.panel1, 8);
            this.panel1.Controls.Add(this.btnNotify);
            this.panel1.Controls.Add(this.btnRead);
            this.panel1.Controls.Add(this.btnWrite);
            this.panel1.Controls.Add(this.btnGetOperation);
            this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.panel1.Location = new System.Drawing.Point(13, 73);
            this.panel1.Name = "panel1";
            this.panel1.Size = new System.Drawing.Size(773, 34);
            this.panel1.TabIndex = 20;
            // 
            // btnNotify
            // 
            this.btnNotify.Enabled = false;
            this.btnNotify.Location = new System.Drawing.Point(267, 3);
            this.btnNotify.Name = "btnNotify";
            this.btnNotify.Size = new System.Drawing.Size(75, 23);
            this.btnNotify.TabIndex = 3;
            this.btnNotify.Text = "NOTIFY";
            this.btnNotify.UseVisualStyleBackColor = true;
            this.btnNotify.Click += new System.EventHandler(this.btnNotify_Click);
            // 
            // btnRead
            // 
            this.btnRead.Enabled = false;
            this.btnRead.Location = new System.Drawing.Point(186, 3);
            this.btnRead.Name = "btnRead";
            this.btnRead.Size = new System.Drawing.Size(75, 23);
            this.btnRead.TabIndex = 2;
            this.btnRead.Text = "READ";
            this.btnRead.UseVisualStyleBackColor = true;
            this.btnRead.Click += new System.EventHandler(this.btnRead_Click);
            // 
            // btnWrite
            // 
            this.btnWrite.Enabled = false;
            this.btnWrite.Location = new System.Drawing.Point(105, 3);
            this.btnWrite.Name = "btnWrite";
            this.btnWrite.Size = new System.Drawing.Size(75, 23);
            this.btnWrite.TabIndex = 1;
            this.btnWrite.Text = "WRITE";
            this.btnWrite.UseVisualStyleBackColor = true;
            this.btnWrite.Click += new System.EventHandler(this.btnWrite_Click);
            // 
            // btnGetOperation
            // 
            this.btnGetOperation.AutoSize = true;
            this.btnGetOperation.Enabled = false;
            this.btnGetOperation.Location = new System.Drawing.Point(2, 3);
            this.btnGetOperation.Name = "btnGetOperation";
            this.btnGetOperation.Size = new System.Drawing.Size(93, 23);
            this.btnGetOperation.TabIndex = 0;
            this.btnGetOperation.Text = "GET OPERATION";
            this.btnGetOperation.UseVisualStyleBackColor = true;
            this.btnGetOperation.Click += new System.EventHandler(this.btnGetOperation_Click);
            // 
            // FrmMain
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 12F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(800, 450);
            this.Controls.Add(this.tableLayoutPanel1);
            this.Name = "FrmMain";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Load += new System.EventHandler(this.FrmMain_Load);
            this.tableLayoutPanel1.ResumeLayout(false);
            this.tableLayoutPanel1.PerformLayout();
            this.panel1.ResumeLayout(false);
            this.panel1.PerformLayout();
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.TableLayoutPanel tableLayoutPanel1;
        private System.Windows.Forms.Button btnOpen;
        private System.Windows.Forms.ComboBox cmbDevices;
        private System.Windows.Forms.Button btnRefresh;
        private System.Windows.Forms.Button btnGetServices;
        private System.Windows.Forms.Button btnGetCharacteriatics;
        private System.Windows.Forms.ComboBox cmbServices;
        private System.Windows.Forms.ComboBox cmbCharacteristics;
        private System.Windows.Forms.Label lblReceiveBuffer;
        private System.Windows.Forms.Label lblTransmitBuffer;
        private System.Windows.Forms.CheckBox ckbDisplayASCII1;
        private System.Windows.Forms.CheckBox ckbDisplayASCII2;
        private System.Windows.Forms.Button btnClear1;
        private System.Windows.Forms.Button btnClear2;
        private System.Windows.Forms.RichTextBox rtxtReceiveBuffer;
        private System.Windows.Forms.RichTextBox rtxtTransmitBuffer;
        private System.Windows.Forms.Label lblLog;
        private System.Windows.Forms.Panel panel1;
        private System.Windows.Forms.Button btnNotify;
        private System.Windows.Forms.Button btnRead;
        private System.Windows.Forms.Button btnWrite;
        private System.Windows.Forms.Button btnGetOperation;
    }
}

