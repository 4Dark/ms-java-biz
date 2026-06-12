-- V1.24__update_agent_avatars.sql
-- 更新 GENERATOR、EVALUATOR、PLANNER 和 ORCHESTRATOR 的头像

UPDATE ms_ai_dev_agent_profile
SET avatar = '/images/avatar-generator.png'
WHERE role_name = 'GENERATOR';

UPDATE ms_ai_dev_agent_profile
SET avatar = '/images/avatar-evaluator.png'
WHERE role_name = 'EVALUATOR';

UPDATE ms_ai_dev_agent_profile
SET avatar = '/images/avatar-planner.png'
WHERE role_name = 'PLANNER';

UPDATE ms_ai_dev_agent_profile
SET avatar = '/images/avatar-orchestrator.png'
WHERE role_name = 'ORCHESTRATOR';
