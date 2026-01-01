#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import sys
import os
import re
import chardet

def detect_encoding(file_path):
    """检测文件的编码"""
    try:
        with open(file_path, 'rb') as f:
            raw_data = f.read(10000)  # 读取前10000字节来检测编码
            result = chardet.detect(raw_data)
            return result['encoding']
    except:
        return 'utf-8'

def fix_html_encoding(file_path):
    """修复HTML文件的UTF-8编码"""
    try:
        # 检测文件编码
        detected_encoding = detect_encoding(file_path)
        print(f"  检测到编码: {detected_encoding}")
        
        # 尝试用检测到的编码读取文件
        try:
            with open(file_path, 'r', encoding=detected_encoding, errors='ignore') as f:
                content = f.read()
        except:
            # 如果检测的编码不行，尝试常见的编码
            encodings = ['utf-8', 'gbk', 'gb2312', 'gb18030', 'latin-1']
            content = None
            for enc in encodings:
                try:
                    with open(file_path, 'r', encoding=enc, errors='ignore') as f:
                        content = f.read()
                    print(f"  使用编码: {enc}")
                    break
                except:
                    continue
            
            if content is None:
                print(f"  错误: 无法读取文件 {file_path}")
                return False
        
        # 检查是否已经有UTF-8 meta标签
        if re.search(r'<meta\s+charset=["\']UTF-8["\']\s*/?>', content, re.IGNORECASE):
            print(f"  已有UTF-8声明，重新以UTF-8保存")
            # 已经有UTF-8声明，重新以UTF-8保存
            with open(file_path, 'w', encoding='utf-8', newline='') as f:
                f.write(content)
            return True
        
        # 添加UTF-8 meta标签到head部分
        if '<head>' in content:
            new_content = content.replace('<head>', '<head>\n    <meta charset="UTF-8">')
            with open(file_path, 'w', encoding='utf-8', newline='') as f:
                f.write(new_content)
            return True
        else:
            print(f"  警告: {file_path} 中没有找到 <head> 标签")
            return False
            
    except Exception as e:
        print(f"  错误: 处理文件 {file_path} 时出错: {e}")
        return False

if __name__ == '__main__':
    if len(sys.argv) < 2:
        print("用法: python fix_encoding.py <html文件路径>")
        sys.exit(1)
    
    file_path = sys.argv[1]
    
    if not os.path.exists(file_path):
        print(f"错误: 文件不存在: {file_path}")
        sys.exit(1)
    
    if fix_html_encoding(file_path):
        print(f"成功: {file_path} 的UTF-8编码已修复")
        sys.exit(0)
    else:
        sys.exit(1)
