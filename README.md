# Java_skill_dev

## compile command

```bash
javac -d out $(find src -name "*.java")
```

-d --> means create directory for 
out --> directory name
$(find src -name "*.java") --> find all java files in src directory and compile them
-name --> find files with specific name

## run command

```bash
java -cp out com.campus.app.Main
```

-cp --> means classpath
-out --> directory name
-com.campus.app.Main --> package name + class name

why we com.campus.app,model,service?
java follows reverse domain name + project name + folder name to create packages

to avoid naming conflicts

java maintain this architecture pattern for code organization

---

## 🗺️ Tech Career Roadmaps (2026 - 2027 Edition)

Complete, crystal-clear, industry-aligned roadmaps with DSA, System Design, tech stacks, roles & responsibilities, and capstone projects:

👉 **[Master Roadmap Index & Roles Matrix](roadmaps/README.md)**

| # | Roadmap Guide | Domain |
|---|---------------|--------|
| 1 | [DSA Mastery (16 Patterns & Real-World Examples)](roadmaps/01_dsa_roadmap.md) | Universal Foundation |
| 2 | [System Design Roadmap (LLD & HLD)](roadmaps/02_system_design_roadmap.md) | Architecture & Scalability |
| 3 | [Modern Java Backend Developer](roadmaps/03_java_developer_roadmap.md) | Java 21/25+, Spring Boot 3, Loom, Kafka |
| 4 | [Python Full-Stack + AI Engineer](roadmaps/04_python_fullstack_ai_roadmap.md) | Next.js 15, React 19, FastAPI, LangChain |
| 5 | [Data Analyst & BI Engineer](roadmaps/05_data_analytics_roadmap.md) | SQL, Polars, Power BI, dbt, Snowflake |
| 6 | [Data Scientist](roadmaps/06_data_science_roadmap.md) | Math, GBDTs, PyTorch, A/B Testing, SHAP |
| 7 | [Machine Learning Engineer (MLE)](roadmaps/07_machine_learning_engineer_roadmap.md) | MLOps, Feast, Triton, vLLM, TensorRT |
| 8 | [AI & Generative AI Engineer](roadmaps/08_genai_ai_engineer_roadmap.md) | LLMs, GraphRAG, LangGraph, QLoRA, Evals |
| 9 | [DevOps & Cloud Engineer](roadmaps/09_devops_cloud_engineer_roadmap.md) | AWS & Azure, Terraform, K8s, GitOps, SRE |
| 10 | [SQL & PL/SQL Developer](roadmaps/10_sql_plsql_developer_roadmap.md) | Oracle PL/SQL, Bulk Collect, CBO Tuning |


