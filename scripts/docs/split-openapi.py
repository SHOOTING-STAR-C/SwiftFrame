import json
import sys
import io

# 设置标准输出编码为 UTF-8
if sys.platform == 'win32':
    sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')
    sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding='utf-8')

def fix_pubresult_object(data):
    """修复 PubResultObject schema 中的 data 字段定义"""
    if 'components' in data and 'schemas' in data['components']:
        if 'PubResultObject' in data['components']['schemas']:
            pubresult = data['components']['schemas']['PubResultObject']
            if 'properties' in pubresult and 'data' in pubresult['properties']:
                # 将空对象 {} 改为明确的 object 类型
                pubresult['properties']['data'] = {
                    "type": "object",
                    "nullable": True
                }

def split_openapi_by_module(input_file, modules, output_dir=None):
    """将完整的 OpenAPI 规范按模块拆分"""
    
    with open(input_file, 'r', encoding='utf-8') as f:
        data = json.load(f)
    
    # 修复 PubResultObject schema
    fix_pubresult_object(data)
    
    for module in modules:
        print(f"处理模块: {module}")
        
        # 创建副本
        module_data = json.loads(json.dumps(data))
        
        # 过滤路径，只保留属于当前模块的接口
        # 如果模块是 AI，则特殊处理以匹配所有以 'AI' 开头的标签
        paths_to_remove = []
        for path, methods in module_data['paths'].items():
            keep = False
            for method_name, method_data_item in methods.items():
                tags = method_data_item.get('tags', [])
                if module == 'AI':
                    if any(t.startswith('AI') for t in tags):
                        keep = True
                        break
                elif module in tags:
                    keep = True
                    break
            if not keep:
                paths_to_remove.append(path)
        
        for path in paths_to_remove:
            del module_data['paths'][path]

        # 如果模块是 AI，则允许匹配以 'AI' 开头的标签（支持 'AI 聊天', 'AI 模型管理' 等）
        if module == 'AI':
            module_data['tags'] = [tag for tag in data.get('tags', []) if tag['name'].startswith('AI')]
        else:
            module_data['tags'] = [tag for tag in data.get('tags', []) if tag['name'] == module]

        output_file_name = f"openapi-{module}.json"
        output_file = os.path.join(output_dir, output_file_name) if output_dir else output_file_name
        
        with open(output_file, 'w', encoding='utf-8') as f:
            json.dump(module_data, f, ensure_ascii=False, indent=2)
        
        # 验证文件
        with open(output_file, 'r', encoding='utf-8') as f:
            verify_data = json.load(f)
            if verify_data and 'paths' in verify_data:
                print(f"[OK] {module} 的 OpenAPI 规范已生成")
            else:
                print(f"[ERROR] {module} 的 OpenAPI 规范无效")
                return False
    
    return True

if __name__ == '__main__':
    import os
    script_dir = os.path.dirname(os.path.abspath(__file__))
    input_file = os.path.join(script_dir, 'openapi-full.json')
    modules = ['鉴权管理', '用户管理', '角色管理', 'AI']
    
    try:
        success = split_openapi_by_module(input_file, modules, output_dir=script_dir)
        if success:
            print("\n所有模块处理完成")
            sys.exit(0)
        else:
            print("\n处理失败")
            sys.exit(1)
    except Exception as e:
        print(f"错误: {e}")
        sys.exit(1)
