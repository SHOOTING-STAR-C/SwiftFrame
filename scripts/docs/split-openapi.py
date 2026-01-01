import json
import sys
import io

# 设置标准输出编码为 UTF-8
if sys.platform == 'win32':
    sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')
    sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding='utf-8')

def split_openapi_by_module(input_file, modules):
    """将完整的 OpenAPI 规范按模块拆分"""
    
    with open(input_file, 'r', encoding='utf-8') as f:
        data = json.load(f)
    
    for module in modules:
        print(f"处理模块: {module}")
        
        # 创建副本
        module_data = json.loads(json.dumps(data))
        
        # 过滤路径，只保留属于当前模块的接口
        paths_to_remove = []
        for path, methods in module_data['paths'].items():
            keep = False
            for method_name, method_data in methods.items():
                tags = method_data.get('tags', [])
                if module in tags:
                    keep = True
                    break
            if not keep:
                paths_to_remove.append(path)
        
        for path in paths_to_remove:
            del module_data['paths'][path]
        
        # 过滤标签，只保留当前模块的标签
        module_data['tags'] = [tag for tag in data.get('tags', []) if tag['name'] == module]
        
        # 保存到文件
        output_file = f"openapi-{module}.json"
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
    input_file = 'openapi-full.json'
    modules = ['鉴权管理', '用户管理', '角色管理']
    
    try:
        success = split_openapi_by_module(input_file, modules)
        if success:
            print("\n所有模块处理完成")
            sys.exit(0)
        else:
            print("\n处理失败")
            sys.exit(1)
    except Exception as e:
        print(f"错误: {e}")
        sys.exit(1)
