-- This script is responsible for removing FKs and updating user authorities so Visualization works as expected.
-- See Feature DHIS2-7946

-- It does:
-- 1) remove all FKs from all "chart*" tables
-- 2) remove all FKs from "reporttable*" tables
-- 3) move user authorities from chart to visualization
-- 4) move user authorities from reporttable to visualization


-- 1) Removing FKs from all "chart*" tables
DO
$$
DECLARE
  t text;
  tables text[] := array['chart','chart_categorydimensions','chart_categoryoptiongroupsetdimensions','chart_datadimensionitems','chart_dataelementgroupsetdimensions','chart_filters','chart_itemorgunitgroups','chart_organisationunits','chart_orgunitgroupsetdimensions','chart_orgunitlevels','chart_periods','chart_seriesitems','chart_yearlyseries','chartuseraccesses','chartusergroupaccesses'];
  r record;
BEGIN
  FOREACH t IN ARRAY tables LOOP
    FOR r IN (
      SELECT constraint_name FROM information_schema.table_constraints
      WHERE table_name=t
      AND constraint_name LIKE 'fk%'
    ) LOOP
      RAISE INFO '%','dropping '||r.constraint_name||'FROM TABLE '||t;
      EXECUTE CONCAT('ALTER TABLE '||t||' DROP CONSTRAINT '||r.constraint_name);
    END LOOP;
  END LOOP;
END;
$$ LANGUAGE plpgsql;


-- 2) Removing FKs from all "reporttable*" tables
DO
$$
DECLARE
  t text;
  tables text[] := array['reporttable','reporttable_categorydimensions','reporttable_categoryoptiongroupsetdimensions','reporttable_columns','reporttable_datadimensionitems','reporttable_dataelementgroupsetdimensions','reporttable_filters','reporttable_itemorgunitgroups','reporttable_organisationunits','reporttable_orgunitgroupsetdimensions','reporttable_orgunitlevels','reporttable_periods','reporttable_rows','reporttableuseraccesses','reporttableusergroupaccesses'];
  r record;
BEGIN
  FOREACH t IN ARRAY tables LOOP
    FOR r IN (
      SELECT constraint_name FROM information_schema.table_constraints
      WHERE table_name=t
      AND constraint_name LIKE 'fk%'
    ) LOOP
      RAISE INFO '%','dropping '||r.constraint_name||'FROM TABLE '||t;
      EXECUTE CONCAT('ALTER TABLE '||t||' DROP CONSTRAINT '||r.constraint_name);
    END LOOP;
  END LOOP;
END;
$$ LANGUAGE plpgsql;


-- 3) Moving user authorities from chart to visualization
UPDATE public.userroleauthorities SET authority = 'F_VISUALIZATION_PUBLIC_ADD' WHERE authority = 'F_CHART_PUBLIC_ADD';
UPDATE public.userroleauthorities SET authority = 'F_VISUALIZATION_EXTERNAL' WHERE authority = 'F_CHART_EXTERNAL';


-- 4) Moving user authorities from reporttable to visualization
UPDATE public.userroleauthorities SET authority = 'F_VISUALIZATION_PUBLIC_ADD' WHERE authority = 'F_REPORTTABLE_PUBLIC_ADD';
UPDATE public.userroleauthorities SET authority = 'F_VISUALIZATION_EXTERNAL' WHERE authority = 'F_REPORTTABLE_EXTERNAL';
