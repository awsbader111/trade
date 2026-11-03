import React, { useEffect, useState } from 'react';
import { createRoot } from 'react-dom/client';
import axios from 'axios';
import './styles.css';

const emptyStudent = { sname: '', studentClass: '12/A', boy: false };
const api = axios.create({ baseURL: '/api/students', timeout: 12000 });

function App() {
  const [students, setStudents] = useState([]);
  const [form, setForm] = useState({ ...emptyStudent });
  const [editId, setEditId] = useState(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [filter, setFilter] = useState('');

  async function load() {
    setLoading(true);
    try {
      const { data } = await api.get('');
      setStudents(data);
      setError('');
    } catch (e) { setError(explain(e)); }
    finally { setLoading(false); }
  }

  useEffect(() => { load(); }, []);

  function explain(e) {
    if (e.response?.status === 400) return 'Enter a student name, class, and gender.';
    if (e.response?.status === 404) return 'Student not found. Refresh the list.';
    return `Request failed: ${e.message || 'unknown error'}`;
  }
  function reset() { setForm({ ...emptyStudent }); setEditId(null); }
  function beginEdit(student) {
    setForm({ sname: student.sname, studentClass: student.studentClass, boy: student.boy });
    setEditId(student.id); setError(''); setMessage('');
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }
  async function save(event) {
    event.preventDefault(); setSaving(true); setError(''); setMessage('');
    try {
      if (editId === null) {
        await api.post('', form); setMessage('Student created.');
      } else {
        await api.put(`/${editId}`, form); setMessage('Student updated.');
      }
      reset();
      await load();
    } catch (e) { setError(explain(e)); }
    finally { setSaving(false); }
  }
  async function remove(student) {
    if (!window.confirm(`Delete ${student.sname}?`)) return;
    setError(''); setMessage('');
    try {
      await api.delete(`/${student.id}`);
      setMessage('Student deleted.');
      if (editId === student.id) reset();
      await load();
    } catch (e) { setError(explain(e)); }
  }
  const displayed = students.filter(s =>
    `${s.sname} ${s.studentClass} ${s.id}`.toLowerCase().includes(filter.toLowerCase()));

  return <>
    <header className="topbar"><strong>AtlasFX · Currency Intelligence</strong>
      <nav aria-label="Main navigation">
        <a href="/">Home</a><a href="/exercise">SOAP · MNB</a>
        <a href="/account_info">Forex Account</a><a href="/actual_prices">Actual Prices</a>
        <a href="/hist_prices">Historical Prices</a><a href="/open_position">Open Trade</a>
        <a href="/positions">Open Positions</a><a href="/close_position">Close Trade</a>
        <a href="/test">Test · Calculator</a>
        <a href="/react/index.html" aria-current="page">React · Class Record</a>
      </nav>
    </header>
    <main className="content">
      <div className="eyebrow">React + Axios + Spring Boot + H2 · Professor-supplied database</div>
      <h1>Class Record — Students</h1>
      <p>CRUD for the <code>students</code> table from the professor's <em>04-Class record</em>
        database. The original 429 student records are loaded once into a file-based H2 database.</p>
      {error && <div className="alert" role="alert">{error}</div>}
      {message && <div className="success" role="status">{message}</div>}
      <div className="layout">
        <section className="panel"><h2>{editId === null ? 'Add student' : 'Edit student'}</h2>
          <form onSubmit={save}>
            <label>Student name
              <input required maxLength="120" value={form.sname}
                onChange={e => setForm({ ...form, sname: e.target.value })}/>
            </label>
            <label>Class
              <input required maxLength="20" value={form.studentClass}
                onChange={e => setForm({ ...form, studentClass: e.target.value })}
                placeholder="12/B"/>
            </label>
            <label>Gender (source column: boy)
              <select value={String(form.boy)}
                onChange={e => setForm({ ...form, boy: e.target.value === 'true' })}>
                <option value="false">Girl (0 in source)</option>
                <option value="true">Boy (-1 in source)</option>
              </select>
            </label>
            <div className="buttons">
              <button disabled={saving} type="submit">{saving ? 'Saving…' : editId === null ? 'Create student' : 'Save changes'}</button>
              {editId !== null && <button type="button" className="secondary" onClick={reset}>Cancel</button>}
            </div>
          </form>
        </section>
        <section className="panel records">
          <div className="section-head"><div><h2>Students</h2><span>{students.length} records</span></div>
            <button className="secondary" type="button" onClick={load}>Refresh</button></div>
          <label className="filter">Search by student, class, or ID
            <input value={filter} onChange={e => setFilter(e.target.value)} placeholder="e.g. 12/B"/>
          </label>
          {loading ? <p role="status">Loading students…</p>
            : displayed.length === 0 ? <p>No matching students.</p>
            : <div className="table-wrap"><table>
                <thead><tr><th>ID</th><th>Student name</th><th>Class</th><th>Gender</th><th>Actions</th></tr></thead>
                <tbody>{displayed.map(s => <tr key={s.id}>
                  <td>{s.id}</td><td><b>{s.sname}</b></td><td>{s.studentClass}</td>
                  <td>{s.boy ? 'Boy' : 'Girl'}</td>
                  <td><div className="buttons">
                    <button type="button" className="secondary" onClick={() => beginEdit(s)}>Edit</button>
                    <button type="button" className="danger" onClick={() => remove(s)}>Delete</button>
                  </div></td>
                </tr>)}</tbody>
              </table></div>}
        </section>
      </div>
    </main>
    <footer className="footer">Java Applications – Lecture Homework · Salokhov Jahongir — PQJEK6 &amp; Bader Aws Basel Saleem — ZUKGOB · John von Neumann University</footer>
  </>;
}

createRoot(document.getElementById('root')).render(<React.StrictMode><App/></React.StrictMode>);
