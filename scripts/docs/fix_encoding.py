#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
为 HTML 文档添加 UTF-8 编码声明
"""

import sys
import os
import re


def fix_html_encoding(file_path):
    """为 HTML 文件添加 UTF-8 编码声明"""
    try:
        # 检查文件是否存在
        if not os.path.exists(file_path):
            print(f"❌ 文件不存在: {file_path}")
            return False
        
        # 读取文件内容
        with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
            content = f.read()
        
        # 检查是否已经有 UTF-8 meta 标签
        if re.search(r'<meta\s+charset=["\']UTF-8["\']\s*/?>', content, re.IGNORECASE):
            # 已经有 UTF-8 声明，只需要重新以 UTF-8 保存
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write(content)
            return True
        
        # 添加 UTF-8 meta 标签到 head 部分
        if '<head>' in content:
            new_content = content.replace(
                '<head>',
                '<head>\n    <meta charset="UTF-8">'
            )
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write(new_content)
            return True
        else:
            print(f"❌ HTML 文件中没有找到 <head> 标签: {file_path}")
            return False
    
    except Exception as e:
        print(f"❌ 处理文件时出错 {file_path}: {e}")
        return False


def main():
    """主函数"""
    if len(sys.argv) < 2:
        print("用法: python fix_encoding.py <html文件路径>")
        return 1
    
    file_path = sys.argv[1]
    
    if fix_html_encoding(file_path):
        return 0
    else:
        return 1


if __name__ == '__main__':
    sys.exit(main())
