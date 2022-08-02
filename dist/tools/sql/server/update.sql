ALTER TABLE items ADD COLUMN visualId int(7) NOT NULL AFTER  agathion_energy;
ALTER TABLE bbsbuffer_buffs ADD COLUMN item_id int(6) NOT NULL DEFAULT 57 AFTER duration_minutes;
ALTER TABLE bbsbuffer_buffs ADD COLUMN item_count int(10) NOT NULL DEFAULT 1 AFTER item_id;